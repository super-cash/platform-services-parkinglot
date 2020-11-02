package cash.super_.platform.service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
@ConfigurationProperties("cash.super.platform.distancematrix")
public class DistanceMatrixProperties {

  @NotNull
  @NotBlank
  private String googleMapsApiToken;

  @NotBlank
  @NotNull
  private String language;

  @NotBlank
  @NotNull
  private String apiVersion;

  public String getGoogleMapsApiToken() {
    return googleMapsApiToken;
  }

  public void setGoogleMapsApiToken(String googleMapsApiToken) {
    this.googleMapsApiToken = googleMapsApiToken;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

}
