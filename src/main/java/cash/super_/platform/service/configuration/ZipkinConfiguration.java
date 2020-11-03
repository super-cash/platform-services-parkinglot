package cash.super_.platform.service.configuration;

import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Only start Zipkin HTTP transport if it is enabled. It's disabled in Dev by default, only enabled in Preprod.
 *
 * @author marcellodesales
 *
 */
@Component
@ConditionalOnProperty(prefix = "spring.zipkin", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ZipkinConfiguration {

  // https://stackoverflow.com/questions/33744875/spring-boot-how-to-log-all-requests-and-responses-with-exceptions-in-single-pl/64607940#64607940
  @Bean
  public HttpTraceRepository htttpTraceRepository() {
    return new InMemoryHttpTraceRepository();
  }
}
