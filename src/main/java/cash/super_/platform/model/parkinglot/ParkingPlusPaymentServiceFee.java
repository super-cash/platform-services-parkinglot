package cash.super_.platform.model.parkinglot;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The payment service fee
 *
 * @author leandromsales
 *
 */
public class ParkingPlusPaymentServiceFee {

    @JsonProperty(value = "service_fee")
    private Long serviceFee;

    private Long parkinglotId;

    public ParkingPlusPaymentServiceFee() {
    }

    public ParkingPlusPaymentServiceFee(Long parkinglotId, Long serviceFee) {
        this.parkinglotId = parkinglotId;
        this.serviceFee = serviceFee;
    }

    public Long getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Long serviceFee) {
        this.serviceFee = serviceFee;
    }

    public Long getParkinglotId() {
        return parkinglotId;
    }

    public void setParkinglotId(Long parkinglotId) {
        this.parkinglotId = parkinglotId;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [serviceFee=" + serviceFee + "]";
    }
}