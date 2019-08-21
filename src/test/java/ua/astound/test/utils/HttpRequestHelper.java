package ua.astound.test.utils;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import lombok.SneakyThrows;

public class HttpRequestHelper {
    @SneakyThrows
    public static String getResponseBody(final String targetUrl) {
        URL url = new URL(targetUrl);
        URLConnection con = url.openConnection();
        InputStream in = con.getInputStream();
        String encoding = con.getContentEncoding();
        encoding = encoding == null ? "UTF-8" : encoding;
        String body = IOUtils.toString(in, encoding);
        return body;
    }
}
