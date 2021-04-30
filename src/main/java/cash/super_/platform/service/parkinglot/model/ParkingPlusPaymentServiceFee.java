package cash.super_.platform.service.parkinglot.model;

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

    public ParkingPlusPaymentServiceFee() {
    }

    public ParkingPlusPaymentServiceFee(Long serviceFee) {
        this.serviceFee = serviceFee;
    }

    public Long getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Long serviceFee) {
        this.serviceFee = serviceFee;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [serviceFee=" + serviceFee + "]";
    }
}