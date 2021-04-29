package cash.super_.platform.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class URLUtils {

    private static final Logger LOG = LoggerFactory.getLogger(URLUtils.class);

    public static URL validateURL(String urlStr, int defaultPort) {
        URL url = null;
        try {
            url = new URL(urlStr);
            if (url.getPort() == -1) {
                return new URL(url.getProtocol(), url.getHost(), defaultPort, url.getFile());
            }
            return url;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static URL validateURLWithException(String urlStr, int defaultPort) throws MalformedURLException {
        URL url = validateURL(urlStr, defaultPort);
        if (url == null) {
            String message = "Invalid URL: " + urlStr;
            LOG.error(message);
            throw new MalformedURLException(message);
        }
        return url;
    }
}
