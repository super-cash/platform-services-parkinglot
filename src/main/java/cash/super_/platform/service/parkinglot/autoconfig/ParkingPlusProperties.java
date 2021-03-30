package cash.super_.platform.service.parkinglot.autoconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import feign.Logger.Level;

@Validated
@Component
@ConfigurationProperties("cash.super.platform.service.parkinglot")
public class ParkingPlusProperties extends SupercashServiceProperties {

  @Min(0)
  private long apiKeyId;

  @Min(0)
  private long parkingLotId;

  @NotBlank
  private String userKey;

  @NotBlank
  private String url;

  private Level clientLogLevel = Level.BASIC;

  @Min(1)
  private long salesCacheDuration;

  @NotNull
  private TimeUnit salesCacheTimeUnit;

  @NotNull
  private Long saleId = Long.valueOf(0);

  @NotNull
  private String ticketItemTitle;

  @NotNull
  private String serviceFeeItemTitle;

  @NotNull
  private String clientRecipientId;

  @NotNull
  private String ourRecipientId;

  @Min(0)
  @Max(100)
  private Double clientPercentage = Double.valueOf(0.0);

  @Min(0)
  @Max(100)
  private Double ourPercentage;

  @NotNull
  private Integer ourFee;

  private String saleNameStartWith = "SUPERCASH";

  @NotNull
  private Integer gracePeriod;

  private List<String> retryableDestinationHosts = new ArrayList<String>();

  @NotNull
  private int retryMaxAttempt;

  @NotNull
  private long retryInterval;

  @NotNull
  private long retryMaxPeriod;

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

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
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

  public Long getSaleId() { return saleId; }

  public void setSaleId(Long saleId) { this.saleId = saleId; }

  public String getTicketItemTitle() {
    return ticketItemTitle;
  }

  public void setTicketItemTitle(String ticketItemTitle) {
    this.ticketItemTitle = ticketItemTitle;
  }

  public String getServiceFeeItemTitle() {
    return serviceFeeItemTitle;
  }

  public void setServiceFeeItemTitle(String serviceFeeItemTitle) {
    this.serviceFeeItemTitle = serviceFeeItemTitle;
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

  public Double getClientPercentage() {
    return clientPercentage;
  }

  public void setClientPercentage(Double clientPercentage) {
    this.clientPercentage = clientPercentage;
  }

  public Double getOurPercentage() {
    return ourPercentage;
  }

  public void setOurPercentage(Double ourPercentage) {
    this.ourPercentage = ourPercentage;
  }

  public Integer getOurFee() {
    return ourFee;
  }

  public void setOurFee(Integer ourFee) {
    this.ourFee = ourFee;
  }

  public String getSaleNameStartWith() {
    return saleNameStartWith;
  }

  public void setSaleNameStartWith(String saleNameStartWith) {
    this.saleNameStartWith = saleNameStartWith;
  }

  public Integer getGracePeriod() {
    return gracePeriod;
  }

  public void setGracePeriod(Integer gracePeriod) {
    this.gracePeriod = gracePeriod;
  }

  public List<String> getRetryableDestinationHosts() {
    return retryableDestinationHosts;
  }

  public void setRetryableDestinationHosts(List<String> retryableDestinationHosts) {
    this.retryableDestinationHosts = retryableDestinationHosts;
  }

  public void addRetryableDestinationHosts(String retryableDestinationHost) {
    this.retryableDestinationHosts.add(retryableDestinationHost);
  }

  public int getRetryMaxAttempt() {
    return retryMaxAttempt;
  }

  public void setRetryMaxAttempt(int retryMaxAttempt) {
    this.retryMaxAttempt = retryMaxAttempt;
  }

  public long getRetryInterval() {
    return retryInterval;
  }

  public void setRetryInterval(long retryInterval) {
    this.retryInterval = retryInterval;
  }

  public long getRetryMaxPeriod() {
    return retryMaxPeriod;
  }

  public void setRetryMaxPeriod(long retryMaxPeriod) {
    this.retryMaxPeriod = retryMaxPeriod;
  }

  @Override
  public String toString() {
    return "ParkingPlusProperties{" +
            "apiKeyId=" + apiKeyId +
            ", parkingLotId=" + parkingLotId +
            ", userKey='" + userKey + '\'' +
            ", url='" + url + '\'' +
            ", clientLogLevel=" + clientLogLevel +
            ", salesCacheDuration=" + salesCacheDuration +
            ", salesCacheTimeUnit=" + salesCacheTimeUnit +
            ", itemTitle='" + ticketItemTitle + '\'' +
            ", clientRecipientId='" + clientRecipientId + '\'' +
            ", ourRecipientId='" + ourRecipientId + '\'' +
            ", clientPercentage=" + clientPercentage +
            ", ourPercentage=" + ourPercentage +
            ", ourFee=" + ourFee +
            ", saleNameStartWith=" + saleNameStartWith +
            ", gracePeriod=" + gracePeriod +
            ", retryableDestinationHosts=" + retryableDestinationHosts +
            ", retryMaxAttempt=" + retryMaxAttempt +
            ", retryInterval=" + retryInterval +
            ", retryMaxPeriod=" + retryMaxPeriod +
            '}';
  }
}
