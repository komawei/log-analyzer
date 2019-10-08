package me.komawei.qiniuloganalyzer.dao;

import me.komawei.qiniuloganalyzer.entity.QiniuAccessLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QiniuAccessLogDao {

    int deleteByFileName(String fileName);

    int saveBatch(List<QiniuAccessLog> qiniuAccessLogs);
}
