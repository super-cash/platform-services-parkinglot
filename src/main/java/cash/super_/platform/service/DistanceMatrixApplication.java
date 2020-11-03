package cash.super_.platform.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DistanceMatrixApplication {

  // https://stackoverflow.com/questions/33744875/spring-boot-how-to-log-all-requests-and-responses-with-exceptions-in-single-pl/64607940#64607940
  @Bean
  public HttpTraceRepository htttpTraceRepository() {
    return new InMemoryHttpTraceRepository();
  }

  public static void main(String args[]) {
    SpringApplication.run(DistanceMatrixApplication.class, args);
    // TODO: When in Preprod or Prod, just turn the banner off app.setBannerMode(Banner.Mode.OFF)
  }
}
