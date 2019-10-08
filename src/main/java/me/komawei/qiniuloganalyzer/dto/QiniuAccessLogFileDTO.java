package me.komawei.qiniuloganalyzer.dto;

import lombok.Data;

/**
 * @ClassName: QiniuAccessLogFileDTO
 * @Description: TODO
 * @author: SimonWayne
 * @date: 2019/9/26 16:24
 */
@Data
public class QiniuAccessLogFileDTO {

    private String name;

    private Integer size;

    private Integer mTime;

    private String url;
}
