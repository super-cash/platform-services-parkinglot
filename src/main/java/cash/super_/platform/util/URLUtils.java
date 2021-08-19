package cash.super_.platform.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class URLUtils {

    private static final Logger LOG = LoggerFactory.getLogger(URLUtils.class);

    public static URL validateURL(String urlStr) {
        URL url = null;
        try {
            url = new java.net.URL(urlStr);
            if (url.getPort() > 1) {
                return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile());
            }
            return url;
        } catch (MalformedURLException e) {
            LOG.error("{} and/or port {} is/are invalid: {}", urlStr, url.getPort(), e.getMessage());
            return null;
        }
    }

    public static URL validateURLWithException(String urlStr) throws MalformedURLException {
        URL url = validateURL(urlStr);
        if (url == null) {
            String message = "Invalid URL: " + urlStr;
            LOG.error(message);
            throw new MalformedURLException(message);
        }
        return url;
    }
}
