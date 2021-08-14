package cash.super_;

import cash.super_.platform.autoconfig.ClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients()
@EnableJpaRepositories(basePackages = {"cash.super_.platform.model.parkinglot",
        "cash.super_.platform.service.parkinglot.repository"})
public class Application {

  @Autowired
  private ClientProperties clientProperties;

  public static void main(String args[]) {
    SpringApplication.run(Application.class, args);
    // TODO: When in Preprod or Prod, just turn the banner off app.setBannerMode(Banner.Mode.OFF)
  }
}
