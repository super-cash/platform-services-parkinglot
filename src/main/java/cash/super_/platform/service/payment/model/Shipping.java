package cash.super_.platform.service.payment.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Shipping {

    private String name;

    private Integer fee;

    @JsonProperty("delivery_date")
    private String deliveryDate;

    private Boolean expedited;

    private Address address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Boolean getExpedited() {
        return expedited;
    }

    public void setExpedited(Boolean expedited) {
        this.expedited = expedited;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Shipping{" +
                "name='" + name + '\'' +
                ", fee=" + fee +
                ", deliveryDate='" + deliveryDate + '\'' +
                ", expedited=" + expedited +
                ", address=" + address +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shipping)) return false;
        Shipping shipping = (Shipping) o;
        return name.equals(shipping.name) && fee.equals(shipping.fee) && deliveryDate.equals(shipping.deliveryDate) &&
                expedited.equals(shipping.expedited) && address.equals(shipping.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fee, deliveryDate, expedited, address);
    }
}
