package cash.super_.platform.service.parkinglot;

import cash.super_.platform.error.supercash.*;
import cash.super_.platform.error.supercash.SupercashInvalidValueException;
import cash.super_.platform.service.parkinglot.model.Marketplace;
import cash.super_.platform.service.parkinglot.repository.MarketplaceRepository;
import cash.super_.platform.utils.IsNumber;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;


public class RequestInterceptorAdapter extends HandlerInterceptorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestInterceptorAdapter.class);

    private MarketplaceRepository marketplaceRepository = null;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (request.getMethod().equals("OPTIONS")) return true;

        String headerName;
        Double value;

        /* Validating transaction id */
        headerName = "X-Supercash-Tid";
        if (Strings.isNullOrEmpty(request.getHeader(headerName))) {
            throw new SupercashInvalidValueException("Transaction ID must be provided.");
        }
        LOG.debug("{}: {}", headerName, request.getHeader(headerName));

        /* Validating user id */
        headerName = "X-Supercash-Uid";
        value = IsNumber.stringIsDoubleWithException(request.getHeader(headerName), headerName);
        LOG.debug("{}: {}", headerName, value.longValue());

        /* Validating marketplace id */
        headerName = "X-Supercash-MarketplaceId";
        value = IsNumber.stringIsDoubleWithException(request.getHeader(headerName), headerName);
        LOG.debug("{}: {}", headerName, value.longValue());

        /* Validating app version and marketplace Id in the database */
        headerName = "X-Supercash-App-Version";
        Optional<Marketplace> marketplaceOpt = marketplaceRepository.findById(value.longValue());
        Double appMinimalVersion = null;
        if (marketplaceOpt.isPresent()) {
            appMinimalVersion = marketplaceOpt.get().getAppVersion();
        } else {
            throw new SupercashMarketplaceNotFoundException("Marketplace with Id '" + value.longValue() + "' " +
                    "not found.");
        }

        value = IsNumber.stringIsDoubleWithException(request.getHeader(headerName), headerName);
        LOG.debug("{}: {}", headerName, value);

        if (value == null || value < appMinimalVersion) {
            SupercashSimpleException exception = new SupercashWrongClientVersionException(headerName +
                    " required is: " + appMinimalVersion + ".");
            exception.addField("required_app_version", appMinimalVersion);
            throw exception;
        }

        /* Validating store id */
        /* TODO: Validate if store exists in the database. */
        headerName = "X-Supercash-StoreId";
        value = IsNumber.stringIsDoubleWithException(request.getHeader(headerName), headerName);
        LOG.debug("{}: {}", headerName, value.longValue());

        return true;
    }

    public void setMarketplaceRepository(MarketplaceRepository marketplaceRepository) {
        this.marketplaceRepository = marketplaceRepository;
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
