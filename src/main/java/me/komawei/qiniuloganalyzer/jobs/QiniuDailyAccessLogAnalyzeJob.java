package me.komawei.qiniuloganalyzer.jobs;

import me.komawei.qiniuloganalyzer.dto.QiniuAccessLogFileDTO;
import me.komawei.qiniuloganalyzer.dto.QiniuAccessLogParamDTO;
import me.komawei.qiniuloganalyzer.dto.QiniuAccessLogResultDTO;
import me.komawei.qiniuloganalyzer.entity.QiniuAccessLog;
import me.komawei.qiniuloganalyzer.service.QiniuAccessLogAnalyzeService;
import me.komawei.qiniuloganalyzer.utils.GzipUtils;
import me.komawei.qiniuloganalyzer.utils.HttpUtils;
import me.komawei.qiniuloganalyzer.utils.QiniuUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * @ClassName: QiniuDailyAccessLogAnalyzeJob
 * @Description: TODO
 * @author: SimonWayne
 * @date: 2019/9/26 15:51
 */
@Component
public class QiniuDailyAccessLogAnalyzeJob {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private QiniuAccessLogAnalyzeService qiniuAccessLogAnalyzeService;

    private static final String QINIU_LOG_DOWNLOAD_URL = "http://fusion.qiniuapi.com/v2/tune/log/list";

    private static final DateTimeFormatter ACCESS_LOG_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss", Locale.US);

    private static final DateTimeFormatter ANALYZE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
    /**
     * @author weishimin
     * @description 调用API，下载七牛access_log日志，并存入表中待分析
     * 1. 调用API下载gzip日志文件
     * 2. 解压gzip
     * 3. 转成字符串
     * 4. 按行解析字符串，保存到数据库中
     * @date 2019/9/26 15:53
     * @return
     **/
    @Scheduled(cron = "0 0 1 * * ?")
    public void execute() throws IOException {
        String queryDay = LocalDateTime.now().minusDays(1).format(ANALYZE_DATE_FORMATTER);
        QiniuAccessLogParamDTO paramDTO = new QiniuAccessLogParamDTO();
        paramDTO.setDay(queryDay);
        paramDTO.setDomains("fileserver.eeka.info;yjshow-fileserver.eeka.info");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "QBox " + QiniuUtils.getQiniuAuthorization(QINIU_LOG_DOWNLOAD_URL));

        HttpEntity<QiniuAccessLogParamDTO> entity = new HttpEntity<>(paramDTO, headers);
        ResponseEntity<QiniuAccessLogResultDTO> exchange = restTemplate.exchange(QINIU_LOG_DOWNLOAD_URL, HttpMethod.POST, entity, QiniuAccessLogResultDTO.class);

        QiniuAccessLogResultDTO resultDTO = exchange.getBody();
        if (resultDTO == null) {
            logger.warn("日志文件为空，任务结束");
            return;
        }

        int resultCode = resultDTO.getCode();
        if (resultCode != HttpStatus.OK.value()) {
            logger.warn("获取日志错误->" + resultDTO.getError());
            return;
        }

        Map<String, List<QiniuAccessLogFileDTO>> dataMap = resultDTO.getData();

        for (Entry<String, List<QiniuAccessLogFileDTO>> entry : dataMap.entrySet()) {
            String domainName = entry.getKey();
            List<QiniuAccessLogFileDTO> fileList = entry.getValue();

            for (QiniuAccessLogFileDTO fileDTO : fileList) {
                String fileUrl = fileDTO.getUrl();
                String fileName = fileDTO.getName();

                InputStream inputStream = HttpUtils.downloadFile(fileUrl);
                String fileContent = GzipUtils.gzipToString(new GZIPInputStream(inputStream));

                List<QiniuAccessLog> qiniuAccessLogs = analysisLogFileAndGenerateQiniuAccessLogList(fileContent, domainName, fileName);
                qiniuAccessLogAnalyzeService.processQiniuAccessLogByLogFile(fileName, qiniuAccessLogs);
            }
        }
    }

    private List<QiniuAccessLog> analysisLogFileAndGenerateQiniuAccessLogList(String fileContent, String domainName, String fileName) {
        String[] lines = StringUtils.split(fileContent, "\n");

        if (ArrayUtils.isEmpty(lines)) {
            return Collections.emptyList();
        }

        List<QiniuAccessLog> logs = new ArrayList<>(lines.length);

        for (String line : lines) {
            QiniuAccessLog accessLog = analysisContent(line, domainName, fileName);

            if (accessLog != null) {
                logs.add(accessLog);
            }
        }

        return logs;
    }

    private QiniuAccessLog analysisContent(String content, String domainName, String fileName) {
        Matcher matcher = ACCESS_LOG_PATTERN.matcher(content);
        if (matcher.find()) {
            String visitorIp = matcher.group("visitorIp");
            String responseTime = matcher.group("responseTime");
            String visitType = matcher.group("visitType");
            String visitPath = matcher.group("visitPath");
            String fileSize = matcher.group("fileSize");
            String httpStatusCode = matcher.group("httpStatusCode");
            String visitSource = matcher.group("visitSource");
            String visitTime = matcher.group("visitTime");
            String deviceInfo = matcher.group("deviceInfo");

            QiniuAccessLog accessLog = QiniuAccessLog.builder().visitorIp(visitorIp).domain(domainName).visitPath(visitPath)
                    .visitTime(LocalDateTime.parse(visitTime, ACCESS_LOG_DATE_FORMATTER)).visitType(visitType).deviceInfo(deviceInfo)
                    .httpStatusCode(Integer.parseInt(httpStatusCode)).fileSize(Integer.parseInt(fileSize)).visitSource(visitSource)
                    .responseTime(Integer.parseInt(responseTime)).logFileName(fileName).build();

            return accessLog;
        }

        return null;
    }

    private static final Pattern ACCESS_LOG_PATTERN = Pattern.compile("(?<visitorIp>((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3})" +
            " ([a-z]|[A-Z])* (?<responseTime>\\d*) (\\[)(?<visitTime>.*) (.*)(\\]) (\")(?<visitType>.*) (?<visitPath>.*) (?<protocol>.*)(\") (?<httpStatusCode>\\d+)" +
            " (?<fileSize>\\d+) (\")(?<visitSource>.*)(\") (\")(?<deviceInfo>.*)(\")");
}
