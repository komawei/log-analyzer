package me.komawei.qiniuloganalyzer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @ClassName: HttpUtils
 * @Description: TODO
 * @author: SimonWayne
 * @date: 2019/9/27 8:54
 */
public class HttpUtils {

    private HttpUtils() {

    }

    public static InputStream downloadFile(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);

        URLConnection conn = url.openConnection();
        InputStream inStream = conn.getInputStream();

        return inStream;
    }
}
