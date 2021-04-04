package cash.super_.platform.service.parkinglot;

import cash.super_.platform.error.supercash.SupercashInvalidValueException;
import cash.super_.platform.error.supercash.SupercashMissingArgumentException;
import cash.super_.platform.error.supercash.SupercashSimpleException;
import cash.super_.platform.error.supercash.SupercashWrongClientVersionException;
import cash.super_.platform.utils.IsNumber;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestInterceptorAdapter extends HandlerInterceptorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestInterceptorAdapter.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String headerName;

        /* Validating transaction id */
        headerName = "X-Supercash-Tid";
        if (Strings.isNullOrEmpty(request.getHeader(headerName))) {
            throw new SupercashInvalidValueException("Transaction ID must be provided.");
        }
        LOG.debug("{}: {}", headerName, request.getHeader(headerName));

        /* Validating user id */
        headerName = "X-Supercash-Uid";
        Double value = IsNumber.stringIsDoubleWithException(request.getHeader(headerName), headerName);
        LOG.debug("{}: {}", headerName, value);

        /* Validating marketplace id */
        headerName = "X-Supercash-MarketplaceId";
        value = IsNumber.stringIsDoubleWithException(request.getHeader(headerName), headerName);
        LOG.debug("{}: {}", headerName, value);

        /* Validating store id */
        headerName = "X-Supercash-StoreId";
        value = IsNumber.stringIsDoubleWithException(request.getHeader(headerName), headerName);
        LOG.debug("{}: {}", headerName, value);

        /* Validating app version */
        headerName = "X-Supercash-App-Version";
        Double appMinimalVersion = 1.0; // get minimal version for the current marketplace
        value = IsNumber.stringIsDoubleWithException(request.getHeader(headerName), headerName);
        LOG.debug("{}: {}", headerName, value);
        if (value < appMinimalVersion) {
            SupercashSimpleException exception = new SupercashWrongClientVersionException(headerName +
                    " must be at least " + appMinimalVersion + ".");
            exception.addField("app_version", appMinimalVersion);
            throw exception;
        }

        return true;
    }

//    private String getParameters(HttpServletRequest request) {
//        StringBuffer posted = new StringBuffer();
//        Enumeration<?> e = request.getParameterNames();
//        if (e != null) {
//            posted.append("?");
//        }
//        while (e.hasMoreElements()) {
//            if (posted.length() > 1) {
//                posted.append("&");
//            }
//            String curr = (String) e.nextElement();
//            posted.append(curr + "=");
//            if (curr.contains("password")
//                    || curr.contains("pass")
//                    || curr.contains("pwd")) {
//                posted.append("*****");
//            } else {
//                posted.append(request.getParameter(curr));
//            }
//        }
//        String ip = request.getHeader("X-FORWARDED-FOR");
//        String ipAddr = (ip == null) ? getRemoteAddr(request) : ip;
//        if (ipAddr!=null && !ipAddr.equals("")) {
//            posted.append("&_psip=" + ipAddr);
//        }
//        return posted.toString();
//    }
//
//    private String getRemoteAddr(HttpServletRequest request) {
//        String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
//        if (ipFromHeader != null && ipFromHeader.length() > 0) {
//            LOG.debug("IP from proxy - X-FORWARDED-FOR: {}", ipFromHeader);
//            return ipFromHeader;
//        }
//        return request.getRemoteAddr();
//    }
}
