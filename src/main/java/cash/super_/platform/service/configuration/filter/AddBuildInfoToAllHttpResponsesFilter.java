package cash.super_.platform.service.configuration.filter;

import cash.super_.platform.autoconfig.ParkingPlusProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Adds the build info to all HTTP Response headers for verification
 * https://www.baeldung.com/spring-response-header
 *
 * HTTP/1.1 200
 * X-B3-TraceId: 9c77d5f8c0fccd10
 * X-Supercash-Build-Time: 2021-03-02 06:17:28.882
 * X-Supercash-Build-Version: 1212223-develop
 * X-Supercash-Api-Version: v1
 */
@Component
public class AddBuildInfoToAllHttpResponsesFilter implements Filter {

    @Autowired
    BuildProperties buildProperties;

    @Autowired
    ParkingPlusProperties parkingPlusProperties;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//        httpServletResponse.setHeader("X-Supercash-Build-Time", buildProperties.getTime().toString().replaceAll("[TZ]", " "));
        String buildVersion = String.join("-", new String[]{ buildProperties.get("git.commit"),
                buildProperties.get("git.branch")});
        httpServletResponse.setHeader("X-Supercash-Build-Version", buildVersion);
        httpServletResponse.setHeader("X-Supercash-Api-Version", parkingPlusProperties.getApiVersion());
        chain.doFilter(request, response);
    }
}
