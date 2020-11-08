package cash.super_.platform.service.parkingplus;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import cash.super_.platform.service.parkingplus.util.SecretsUtil;

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

}
