package cash.super_.platform.service.parkingplus;

import java.util.concurrent.TimeUnit;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import cash.super_.platform.service.parkingplus.util.SecretsUtil;
import feign.Logger.Level;

@Validated
@Component
@ConfigurationProperties("cash.super.platform.service.parkingplus")
public class ParkingPlusProperties {

  @Min(0)
  private long apiKeyId;

  @Min(0)
  private long parkingLotId;

  @NotBlank
  private String userKey;

  @NotBlank
  private String apiVersion;

  private String host;

  private Level clientLogLevel;

  @Min(1)
  private long salesCacheDuration;

  @NotNull
  private TimeUnit salesCacheTimeUnit;

  public String getUserKey() {
    return userKey;
  }

  public void setUserKey(String userKey) {
    this.userKey = userKey;
  }

  public Long getApiKeyId() {
    return apiKeyId;
  }

  public void setApiKeyId(long apiKeyId) {
    this.apiKeyId = apiKeyId;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public long getParkingLotId() {
    return parkingLotId;
  }

  public void setParkingLotId(long parkingLotId) {
    this.parkingLotId = parkingLotId;
  }

  public String getHost() {
    return host == null ? "https://demonstracao.parkingplus.com.br/servicos" : host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  @Override
  public String toString() {
    return "ParkingPlusProperties [apiKeyId=" + apiKeyId + ", parkingLotId=" + parkingLotId + ", userKey="
        + SecretsUtil.obsfucate(userKey) + ", apiVersion=" + apiVersion + "]";
  }

  public Level getClientLogLevel() {
    return clientLogLevel == null ? Level.BASIC : clientLogLevel;
  }

  public void setClientLogLevel(Level clientLogLevel) {
    this.clientLogLevel = clientLogLevel;
  }

  public long getSalesCacheDuration() {
    return salesCacheDuration;
  }

  public void setSalesCacheDuration(long salesCacheDuration) {
    this.salesCacheDuration = salesCacheDuration;
  }

  public TimeUnit getSalesCacheTimeUnit() {
    return salesCacheTimeUnit;
  }

  public void setSalesCacheTimeUnit(TimeUnit salesCacheTimeUnit) {
    this.salesCacheTimeUnit = salesCacheTimeUnit;
  }
}
