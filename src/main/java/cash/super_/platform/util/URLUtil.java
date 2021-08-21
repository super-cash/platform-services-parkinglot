package cash.super_.platform.util;

import java.net.MalformedURLException;
import java.net.URL;

public enum URLUtil {

    ;

    public static int getPort(URL url) {
        return url.getPort() == -1 ? (url.getProtocol().equals("https") ? 443 : 80) : url.getPort();
    }

    public static URL validateURL(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        if (url.getPort() > 1) {
            return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile());
        }
        return url;
    }

    public static URL validateURLWithException(String urlStr) throws MalformedURLException {
        URL url = validateURL(urlStr);
        if (url == null) {
            String message = "Invalid URL: " + urlStr;
            throw new MalformedURLException(message);
        }
        return url;
    }
}
