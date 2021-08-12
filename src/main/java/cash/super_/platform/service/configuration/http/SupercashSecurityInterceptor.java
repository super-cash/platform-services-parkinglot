package cash.super_.platform.service.configuration.http;

import cash.super_.platform.error.supercash.SupercashInvalidValueException;
import cash.super_.platform.error.supercash.SupercashMarketplaceNotFoundException;
import cash.super_.platform.service.parkinglot.AbstractController;
import cash.super_.platform.service.parkinglot.repository.MarketplaceRepository;
import cash.super_.platform.utils.NumberUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

/**
 * The SupercashSecurityInterceptor is the web security of calls for Supercash.
 * Based on https://dzone.com/articles/using-requestscope-with-your-api, https://gitlab.com/johnjvester/request-scope.
 */
public class SupercashSecurityInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(SupercashSecurityInterceptor.class);

    @Resource(name = "supercashRequestContextInstance")
    private SupercashRequestContext requestContext;

    @Autowired
    private MarketplaceRepository marketplaceRepository;

    @Value("/${cash.super.platform.service.parkinglot.apiVersion}")
    private String apiVersion;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("OPTIONS")) return true;

        // TODO: REPLACE WITH SPRING SECURITY!
        // https://www.baeldung.com/spring-boot-security-autoconfiguration
        // Verify if any of the requested urls are valid according to our supported URLs
        String requestedPath = request.getRequestURI();
        String protectedPaths = String.format("/%s/%s", apiVersion, AbstractController.BASE_ENDPOINT);
        // If it is not in the controller, allow them because it can be actuator, swagger, etc
        if (!requestedPath.contains(protectedPaths)) {
            return true;
        }

        /* Validating transaction id */
        String headerName = "X-Supercash-Tid";
        if (Strings.isNullOrEmpty(request.getHeader(headerName))) {
            LOG.error("The required header {} was missing", headerName);
            throw new SupercashInvalidValueException("Transaction ID must be provided.");
        }
        final String transactionId = request.getHeader(headerName);

        /* Validating marketplace id */
        headerName = "X-Supercash-MarketplaceId";
        long marketPlaceId = NumberUtil.stringIsLongWithException(request.getHeader(headerName), headerName);

        /* Validating marketplace id */
        headerName = "X-Supercash-StoreId";
        long storeId = NumberUtil.stringIsLongWithException(request.getHeader(headerName), headerName);

        /* Validating app version and marketplace Id in the database */
        headerName = "X-Supercash-App-Version";
        double appVersion = NumberUtil.stringIsDoubleWithException(request.getHeader(headerName), headerName);

        // TODO: Validate the user ID in the DB
        /* Validating user id */
        headerName = "X-Supercash-Uid";
        long userId = NumberUtil.stringIsLongWithException(request.getHeader(headerName), headerName);

        // Update the context that will be provided to all the services
        requestContext.setContext(transactionId, marketPlaceId, storeId, userId, appVersion);

            // Get the variable https://stackoverflow.com/questions/12249721/spring-mvc-3-how-to-get-path-variable-in-an-interceptor/23468496#23468496
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        try {
            // make references locally
            final Double requestedAppVersion = requestContext.getRequestedAppVersion();

            if (!marketplaceRepository.existsDistinctByIdAndAppVersionLessThanEqual(marketPlaceId, requestedAppVersion)) {
                final String errorMsg = "Marketplace with Id '" + marketPlaceId + "' " +
                        "not found at compatible version + '" + appVersion + "'";
                requestContext = null;
                throw new SupercashMarketplaceNotFoundException(errorMsg);
            }

        } catch (Exception error) {
            LOG.error("Request rejected due to error: {}", error.getMessage());
            throw error;
        }
        return true;
    }

}

