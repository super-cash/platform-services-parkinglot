package cash.super_.platform.service.distancematrix;

import java.util.concurrent.TimeUnit;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
@ConfigurationProperties("cash.super.platform.service.distancematrix")
public class DistanceMatrixProperties {

  @NotBlank
  private String googleMapsApiToken;

  @NotBlank
  private String language;

  @NotBlank
  private String apiVersion;

  @Min(1)
  private long resultsCacheDuration;

  @NotNull
  private TimeUnit resultsCacheTimeUnit;

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

  public long getResultsCacheDuration() {
    return resultsCacheDuration;
  }

  public void setResultsCacheDuration(long resultsCacheMillisecondsDuration) {
    this.resultsCacheDuration = resultsCacheMillisecondsDuration;
  }

  public TimeUnit getResultsCacheTimeUnit() {
    return resultsCacheTimeUnit;
  }

  public void setResultsCacheTimeUnit(TimeUnit resultsCacheTimeUnit) {
    this.resultsCacheTimeUnit = resultsCacheTimeUnit;
  }

}
