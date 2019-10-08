package me.komawei.qiniuloganalyzer.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: QiniuAccessLog
 * @Description: TODO
 * @author: SimonWayne
 * @date: 2019/9/27 9:00
 */
@Data
@Builder
public class QiniuAccessLog {

    private Long id;

    /**
     * 域名
     */
    private String domain;

    /**
     * 响应时间
     */
    private Integer responseTime;

    /**
     * 访问者IP
     */
    private String visitorIp;

    /**
     * 访问时间
     */
    private LocalDateTime visitTime;

    /**
     * 访问路径
     */
    private String visitPath;

    /**
     * 访问类型GET/POST
     */
    private String visitType;

    /**
     * HTTP状态码
     */
    private Integer httpStatusCode;

    /**
     * 文件大小
     */
    private Integer fileSize;

    /**
     * 访问来源
     */
    private String visitSource;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 日志文件名，保存时，首先删除该文件名下的所有记录
     */
    private String logFileName;
}
