package cash.super_.platform.model.parkinglot;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The payment service fee
 *
 * @author leandromsales
 *
 */
public class ParkingPlusPaymentGracePeriod {

  @JsonProperty(value = "grace_period")
  private Integer gracePeriod;

  public ParkingPlusPaymentGracePeriod() {
  }

  public ParkingPlusPaymentGracePeriod(Integer gracePeriod) {
    this.gracePeriod = gracePeriod;
  }

  public Integer getGracePeriod() {
    return gracePeriod;
  }

  public void setGracePeriod(Integer gracePeriod) {
    this.gracePeriod = gracePeriod;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [gracePeriod=" + gracePeriod + "]";
  }
}
