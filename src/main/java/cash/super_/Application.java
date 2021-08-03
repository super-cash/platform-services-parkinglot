package cash.super_;

import cash.super_.platform.autoconfig.PlatformConfigurationProperties;
import cash.super_.platform.autoconfig.ParkingPlusProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableFeignClients()
@EnableJpaRepositories(basePackages = {"cash.super_.platform.service.parkinglot.model",
        "cash.super_.platform.service.parkinglot.repository"})
public class Application {

  private static final Logger LOG = LoggerFactory.getLogger(Application.class);

  @Autowired
  private PlatformConfigurationProperties properties;

  @PostConstruct
  public void init(){
    // Setting Spring Boot SetTimeZone
    TimeZone.setDefault(TimeZone.getTimeZone(properties.getTimeZone()));
  }

  public static void main(String args[]) {
    SpringApplication.run(Application.class, args);
    // TODO: When in Preprod or Prod, just turn the banner off app.setBannerMode(Banner.Mode.OFF)
  }
}