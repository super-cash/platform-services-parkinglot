package cash.super_.platform.service.configuration;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import brave.Span;
import brave.Tracer;
import cash.super_.platform.service.parkingplus.AbstractController;

/**
 * Adds a tracing ID into the HTTP response
 * 
 * https://github.com/spring-cloud/spring-cloud-sleuth/issues/633#issuecomment-638367781
 * https://github.com/spring-cloud/spring-cloud-sleuth/issues/1329#issue-430146912
 * 
 * @author marcellodesales
 *
 */
@Component
public class SleuthResponseTraceIdInjectorFilter extends OncePerRequestFilter {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractController.class);

  private static final String TRACE_ID_HEADER = "X-B3-TraceId";

  @Autowired
  private Tracer tracer;

  @Override
  protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                  final FilterChain filterChain) throws ServletException, IOException {

      final Span currentSpan = tracer.currentSpan();
      if (currentSpan != null && StringUtils.isEmpty(response.getHeader(TRACE_ID_HEADER))) {
          final String traceId = currentSpan.context().traceIdString();
          LOG.debug("Added tracing id in response - {}", traceId);
          response.setHeader(TRACE_ID_HEADER, traceId);
      }
      filterChain.doFilter(request, response);
  }

}
