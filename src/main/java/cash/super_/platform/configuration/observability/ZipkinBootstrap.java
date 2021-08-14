package cash.super_.platform.configuration.observability;

import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Bootstraps sending tracing from Sleuth to Zipkin. HTTP transport if it is enabled. 
 * 
 * It's disabled in Dev by default, only enabled in Preprod.
 * https://stackoverflow.com/questions/56525260/disable-distributed-tracing-for-development/56529683#56529683
 * 
 * @author marcellodesales
 *
 */
@Component
@ConditionalOnProperty(prefix = "spring.zipkin", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ZipkinBootstrap {

  // https://juplo.de/actuator-httptrace-does-not-work-with-spring-boot-2-2/
  // https://stackoverflow.com/questions/33744875/spring-boot-how-to-log-all-requests-and-responses-with-exceptions-in-single-pl/64607940#64607940
  @Bean
  public HttpTraceRepository htttpTraceRepository() {
    return new InMemoryHttpTraceRepository();
  }
}
