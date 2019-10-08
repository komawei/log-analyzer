package me.komawei.qiniuloganalyzer.service;

import me.komawei.qiniuloganalyzer.dao.QiniuAccessLogDao;
import me.komawei.qiniuloganalyzer.entity.QiniuAccessLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName: QiniuAccessLogAnalyzeService
 * @Description: TODO
 * @author: SimonWayne
 * @date: 2019/9/27 10:41
 */
@Service
@Transactional
public class QiniuAccessLogAnalyzeService {

    @Autowired
    private QiniuAccessLogDao qiniuAccessLogDao;

    public void processQiniuAccessLogByLogFile(String fileName, List<QiniuAccessLog> qiniuAccessLogs) {
        qiniuAccessLogDao.deleteByFileName(fileName);
        qiniuAccessLogDao.saveBatch(qiniuAccessLogs);
    }
}
