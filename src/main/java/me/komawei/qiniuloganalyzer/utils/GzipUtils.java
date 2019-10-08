package me.komawei.qiniuloganalyzer.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * @ClassName: GzipUtils
 * @Description: TODO
 * @author: SimonWayne
 * @date: 2019/9/26 15:38
 */
public class GzipUtils {

    private GzipUtils() {

    }

    public static String gzipToString(GZIPInputStream gzipInputStream) throws IOException {
        return IOUtils.toString(gzipInputStream);
    }
}
