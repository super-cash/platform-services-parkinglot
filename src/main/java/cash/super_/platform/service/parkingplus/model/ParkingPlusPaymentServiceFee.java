package cash.super_.platform.service.parkingplus.model;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.service.parkingplus.autoconfig.ParkingPlusProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The payment service fee
 *
 * @author leandromsales
 *
 */
public class ParkingPlusPaymentServiceFee {

  @JsonProperty(value = "service_fee")
  private Integer serviceFee;

  public ParkingPlusPaymentServiceFee() {
  }

  public ParkingPlusPaymentServiceFee(Integer serviceFee) {
    this.serviceFee = serviceFee;
  }

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
