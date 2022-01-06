package cash.super_.platform.adapter.http;

import cash.super_.platform.adapter.feign.SupercashSimpleException;
import cash.super_.platform.error.parkinglot.SupercashInvalidValueException;
import cash.super_.platform.util.FieldType;
import cash.super_.platform.util.NumberUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The SupercashSecurityInterceptor is the web security of calls for Supercash.
 * Based on https://dzone.com/articles/using-requestscope-with-your-api, https://gitlab.com/johnjvester/request-scope.
 */
@Component
public class SupercashSecurityInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(SupercashSecurityInterceptor.class);

    @Resource(name = "supercashRequestContextInstance")
    private SupercashRequestContext requestContext;

    @Value("/${cash.super.platform.service.parkinglot.apiVersion}")
    private String apiVersion;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("OPTIONS")) return true;

        // TODO: REPLACE WITH SPRING SECURITY!
        // https://www.baeldung.com/spring-boot-security-autoconfiguration
        // Verify if any of the requested urls are valid according to our supported URLs
        String requestedPath = request.getRequestURI();
        // If it is not in the controller, allow them because it can be actuator, swagger, etc
        if (!requestedPath.startsWith(apiVersion)) {
            return true;
        }

        try {
            /* Validating transaction id */
            String headerName = "X-Supercash-Tid";
            if (Strings.isNullOrEmpty(request.getHeader(headerName))) {
                LOG.error("The required header {} was missing", headerName);
                throw new SupercashInvalidValueException("Transaction ID must be provided.");
            }
            final String transactionId = request.getHeader(headerName);

            /* Validating marketplace id */
            headerName = "X-Supercash-Marketplace-Id";
            long marketPlaceId = NumberUtil.stringIsLongWithException(FieldType.HEADER, request.getHeader(headerName), headerName);

            /* Validating marketplace id */
            headerName = "X-Supercash-Store-Id";
            long storeId = NumberUtil.stringIsLongWithException(FieldType.HEADER, request.getHeader(headerName), headerName);

            /* Validating app version and marketplace Id in the database */
            headerName = "X-Supercash-App-Version";
            double appVersion = NumberUtil.stringIsDoubleWithException(FieldType.HEADER, request.getHeader(headerName), headerName);

            // TODO: Validate the user ID in the DB
            /* Validating user id */
            headerName = "X-Supercash-Uid";
            long userId = NumberUtil.stringIsLongWithException(FieldType.HEADER, request.getHeader(headerName), headerName);

            /* Testing flag */
            headerName = "X-Supercash-Testing";
            List<String> modulesInTestingMode = new ArrayList<>();
            if (!Strings.isNullOrEmpty(request.getHeader(headerName))) {
                modulesInTestingMode = Arrays.asList(request.getHeader(headerName).split("\\s*,\\s*"));
            }

            // Update the context that will be provided to all the services
            requestContext.setContext(transactionId, marketPlaceId, storeId, userId, appVersion, modulesInTestingMode);

            LOG.debug("Created Supercash Request Context: {}", requestContext);

            // Get the variable https://stackoverflow.com/questions/12249721/spring-mvc-3-how-to-get-path-variable-in-an-interceptor/23468496#23468496
            Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

            // make references locally
            final Double requestedAppVersion = requestContext.getRequestedAppVersion();

            // TODO: We need to make sure the marketplace is correct by another trust validation
//            if (!marketplaceRepository.existsDistinctByIdAndAppVersionLessThanEqual(marketPlaceId, requestedAppVersion)) {
//                final String errorMsg = "Marketplace with Id '" + marketPlaceId + "' " +
//                        "not found at compatible version + '" + appVersion + "'";
//                requestContext = null;
//                LOG.error("Error creating context: {}", errorMsg);
//                throw new SupercashMarketplaceNotFoundException(errorMsg);
//            }

        } catch (SupercashSimpleException error) {
            // https://stackoverflow.com/questions/39554740/springboot-how-to-return-error-status-code-in-prehandle-of-handlerinterceptor/39555027#39555027
            LOG.error("Error processing the request: {}", error.toString());
            response.getWriter().write(error.toString());
            response.setStatus(error.SupercashExceptionModel.getAdditionalErrorCode());
            return false;
        }
        return true;
    }

}

