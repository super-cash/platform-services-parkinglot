package cash.super_.platform.service.parkingplus.autoconfig;

import java.util.concurrent.TimeUnit;
import javax.validation.constraints.Max;
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
public class ParkingPlusProperties extends SupercashServiceProperties {

  @Min(0)
  private long apiKeyId;

  @Min(0)
  private long parkingLotId;

  @NotBlank
  private String userKey;

  @NotBlank
  private String host;

  private Level clientLogLevel = Level.BASIC;

  @Min(1)
  private long salesCacheDuration;

  @NotNull
  private TimeUnit salesCacheTimeUnit;

  @NotNull
  private String itemTitle;

  @NotNull
  private String clientRecipientId;

  @NotNull
  private String ourRecipientId;

  @Min(0)
  @Max(100)
  private Integer clientPercentage;

  @Min(0)
  @Max(100)
  private Integer ourPercentage;

  @NotNull
  private Integer ourFee;

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

  public long getParkingLotId() {
    return parkingLotId;
  }

  public void setParkingLotId(long parkingLotId) {
    this.parkingLotId = parkingLotId;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Level getClientLogLevel() {
    return clientLogLevel;
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

  public String getItemTitle() {
    return itemTitle;
  }

  public void setItemTitle(String itemTitle) {
    this.itemTitle = itemTitle;
  }

  public String getClientRecipientId() {
    return clientRecipientId;
  }

  public void setClientRecipientId(String clientRecipientId) {
    this.clientRecipientId = clientRecipientId;
  }

  public String getOurRecipientId() {
    return ourRecipientId;
  }

  public void setOurRecipientId(String ourRecipientId) {
    this.ourRecipientId = ourRecipientId;
  }

  public Integer getClientPercentage() {
    return clientPercentage;
  }

  public void setClientPercentage(Integer clientPercentage) {
    this.clientPercentage = clientPercentage;
  }

  public Integer getOurPercentage() {
    return ourPercentage;
  }

  public void setOurPercentage(Integer ourPercentage) {
    this.ourPercentage = ourPercentage;
  }

  public Integer getOurFee() {
    return ourFee;
  }

  public void setOurFee(Integer ourFee) {
    this.ourFee = ourFee;
  }

  @Override
  public String toString() {
    return "ParkingPlusProperties{" +
            "apiKeyId=" + apiKeyId +
            ", parkingLotId=" + parkingLotId +
            ", userKey='" + userKey + '\'' +
            ", host='" + host + '\'' +
            ", clientLogLevel=" + clientLogLevel +
            ", salesCacheDuration=" + salesCacheDuration +
            ", salesCacheTimeUnit=" + salesCacheTimeUnit +
            ", itemTitle='" + itemTitle + '\'' +
            ", clientRecipientId='" + clientRecipientId + '\'' +
            ", ourRecipientId='" + ourRecipientId + '\'' +
            ", clientPercentage=" + clientPercentage +
            ", ourPercentage=" + ourPercentage +
            ", ourFee=" + ourFee +
            '}';
  }
}
