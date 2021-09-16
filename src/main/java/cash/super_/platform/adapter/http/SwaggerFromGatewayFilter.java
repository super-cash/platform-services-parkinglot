package cash.super_.platform.adapter.http;

import com.google.common.base.Strings;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.swagger.common.HostNameProvider;
import springfox.documentation.swagger2.web.SwaggerTransformationContext;
import springfox.documentation.swagger2.web.WebMvcSwaggerTransformationFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Changes the host and path to make sure it returns the proper when returned by the gateway
 */
// make sure it runs after the default plugin springfox.documentation.swagger2.web.WebMvcBasePathAndHostnameTransformationFilter
// Documented at https://github.com/springfox/springfox/issues/3030#issuecomment-920571470
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1000)
public class SwaggerFromGatewayFilter implements WebMvcSwaggerTransformationFilter {

    protected static final Logger LOG = LoggerFactory.getLogger(SwaggerFromGatewayFilter.class);

    @Override
    public boolean supports(DocumentationType delimiter) {
        return delimiter == DocumentationType.SWAGGER_2;
    }

    @Override
    public Swagger transform(SwaggerTransformationContext<HttpServletRequest> context) {
        Swagger swagger = context.getSpecification();
        context.request().ifPresent(servletRequest -> {
            // Map the request headers to their values
            // https://stackoverflow.com/questions/23261803/iterate-an-enumeration-in-java-8/23261952#23261952
            // https://stackoverflow.com/questions/21015889/why-does-getheadernames-returns-an-enumeration-in-httpservletrequest-but-a-colle
            Map<String, String> requestHeaderIndex = Collections.list(servletRequest.getHeaderNames()).stream()
                    .collect(Collectors.toMap(headerName -> headerName, headerName -> servletRequest.getHeader(headerName.toString())));

            UriComponents uriComponents = HostNameProvider.componentsFrom(servletRequest, swagger.getBasePath());

            LOG.debug("Requested with the following headers: {}", requestHeaderIndex);
            // If this was a call from the gateway, use the URL from the current host instead of the server
            swagger.host(requestedHost(uriComponents, servletRequest));

            String basePath;
            if (!StringUtils.isEmpty(uriComponents.getPath())) {
                basePath = uriComponents.getPath();
                // rewrite paths to remove the leading base path from each endpoint
                final Map<String, Path> newPaths = swagger.getPaths().entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getKey().replaceAll("^" + basePath, ""),
                                Map.Entry::getValue));
                swagger.setPaths(newPaths);
            } else {
                basePath = "/";
                // no need to rewrite paths
            }
            swagger.basePath(basePath);
        });
        return swagger;
    }

    /**
     * @param uriComponents the uri components
     * @return the host URL when calls goes directly to the server without any proxy just return the component
     */
    private String requestedHost(UriComponents uriComponents, HttpServletRequest request) {
        // When using an API Gateway, don't return the URI from the gateway, just from the server if serving the UI
        boolean requestedForwardedByProxy = !Strings.isNullOrEmpty(request.getHeader("x-forwarded-host"));
        String host = requestedForwardedByProxy ? request.getRemoteHost() : uriComponents.getHost();
        int port = requestedForwardedByProxy ? request.getServerPort() : uriComponents.getPort();
        if (port > -1) {
            return String.format("%s:%d", host, port);
        }
        return host;
    }
}