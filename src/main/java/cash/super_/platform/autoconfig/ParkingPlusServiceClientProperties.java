package cash.super_.platform.autoconfig;

import java.net.URL;
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

@Validated
@Component
@ConfigurationProperties(ParkingPlusServiceClientProperties.PROPERTY_ROOT_PREFIX)
public class ParkingPlusServiceClientProperties {

  public static final String PROPERTY_ROOT_PREFIX = "cash.super.platform.client.parkingplus";

  @NotNull
  private URL baseUrl;

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
  private Integer gracePeriodInMinutes;

  @NotNull
  private String udidPrefix;

  private Boolean bootstrapData;

  private List<String> retryableDestinationHosts = new ArrayList<>();

  public URL getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(URL baseUrl) {
    this.baseUrl = baseUrl;
  }


  public Boolean getBootstrapData() {
    return bootstrapData;
  }

  public void setBootstrapData(Boolean bootstrapData) {
    this.bootstrapData = bootstrapData;
  }

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

  public Integer getGracePeriodInMinutes() {
    return gracePeriodInMinutes;
  }

  public void setGracePeriodInMinutes(Integer gracePeriodInMinutes) {
    this.gracePeriodInMinutes = gracePeriodInMinutes;
  }

  public String getUdidPrefix() {
    return udidPrefix;
  }

  public void setUdidPrefix(String udidPrefix) {
    this.udidPrefix = udidPrefix;
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
            ", gracePeriod=" + gracePeriodInMinutes +
            ", " + super.toString() +
            '}';
  }
}
