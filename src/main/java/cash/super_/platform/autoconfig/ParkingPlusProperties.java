package cash.super_.platform.autoconfig;

import java.util.concurrent.TimeUnit;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import cash.super_.platform.autoconfig.ServiceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
@ConfigurationProperties("cash.super.platform.service.parkinglot")
public class ParkingPlusProperties extends ServiceProperties {

  @Min(0)
  private long apiKeyId;

  @Min(0)
  private long parkingLotId;

  @NotBlank
  private String userKey;

  @Min(1)
  private long salesCacheDuration;

  @NotNull
  private TimeUnit salesCacheTimeUnit;

  @NotNull
  private Long saleId = 0L;

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
  private Double clientPercentage = 0.0;

  @Min(0)
  @Max(100)
  private Double ourPercentage;

  @NotNull
  private Long ourFee;

  private String saleNameStartWith = "SUPERCASH";

  @NotNull
  private Integer gracePeriod;

  @NotNull
  private String udidPrefix;

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

  public Long getOurFee() {
    return ourFee;
  }

  public void setOurFee(Long ourFee) {
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

  public String getUdidPrefix() {
    return udidPrefix;
  }

  public void setUdidPrefix(String udidPrefix) {
    this.udidPrefix = udidPrefix;
  }

  @Override
  public String toString() {
    return "ParkingPlusProperties{" +
            "apiKeyId=" + apiKeyId +
            ", parkingLotId=" + parkingLotId +
            ", userKey='" + userKey + '\'' +
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
            ", " + super.toString() +
            '}';
  }
}
