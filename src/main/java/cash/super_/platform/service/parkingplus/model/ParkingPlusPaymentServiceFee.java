package cash.super_.platform.service.parkingplus.model;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The payment service fee
 *
 * @author leandromsales
 *
 */
public class ParkingPaymentServiceFee {

  @JsonProperty(value = "service_fee")
  private Integer serviceFee;

  public Integer getServiceFee() {
    return serviceFee;
  }

  public void setServiceFee(Integer serviceFee) {
    this.serviceFee = serviceFee;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [serviceFee=" + serviceFee + "]";
  }
}
