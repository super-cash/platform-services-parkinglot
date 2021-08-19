package cash.super_;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients()
@EnableJpaRepositories(basePackages = {"cash.super_.platform.model.parkinglot",
        "cash.super_.platform.repository"})
public class Application {

  public static void main(String args[]) {
    SpringApplication.run(Application.class, args);
    // TODO: When in Preprod or Prod, just turn the banner off app.setBannerMode(Banner.Mode.OFF)
  }
}
