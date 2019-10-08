package me.komawei.qiniuloganalyzer.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: QiniuAccessLogResultDTO
 * @Description: TODO
 * @author: SimonWayne
 * @date: 2019/9/26 16:13
 */
@Data
public class QiniuAccessLogResultDTO {

    private Integer code;

    private String error;

    private Map<String, List<QiniuAccessLogFileDTO>> data;
}
