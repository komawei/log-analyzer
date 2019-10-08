package me.komawei.qiniuloganalyzer.utils;


import com.qiniu.util.Auth;

import java.net.URI;

/**
 * @ClassName: QiniuUtils
 * @Description: TODO
 * @author: SimonWayne
 * @date: 2019/9/26 16:38
 */
public class QiniuUtils {

    /**
     * 七牛access-key
     */
    private static final String QINIU_ACCESS_KEY = "your-access-key";

    /**
     * 七牛access-secret
     */
    private static final String QINIU_ACCESS_SECRET = "your-access-secret";

    private QiniuUtils() {

    }

    /**
     * @author weishimin
     * @description 创建七牛日志获取请求头Authorization
     * @date 2019/10/8 13:56
     * Parameters: [url]
     * @return
     * @see String
     **/
    public static String getQiniuAuthorization(String url) {
        Auth auth = Auth.create(QINIU_ACCESS_KEY, QINIU_ACCESS_SECRET);
        String path = URI.create(url).getRawPath() + "\n";
        return auth.sign(path);
    }
}
