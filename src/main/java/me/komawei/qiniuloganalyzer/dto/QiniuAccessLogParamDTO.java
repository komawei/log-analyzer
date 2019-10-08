package me.komawei.qiniuloganalyzer.dto;

import lombok.Data;

/**
 * @ClassName: QiniuAccessLogParamDTO
 * @Description: TODO
 * @author: SimonWayne
 * @date: 2019/9/26 16:11
 */
@Data
public class QiniuAccessLogParamDTO {

    /**
     * 日期，例如 2016-07-01
     */
    private String day;

    /**
     * 域名列表，以英文分号 ; 分割
     */
    private String domains;
}
