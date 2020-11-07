package cash.super_.platform.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DistanceMatrixApplication {

  public static void main(String args[]) {
    SpringApplication.run(DistanceMatrixApplication.class, args);
    // TODO: When in Preprod or Prod, just turn the banner off app.setBannerMode(Banner.Mode.OFF)
  }
}
