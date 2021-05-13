package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "payment_address")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @JsonProperty(required = true)
    private String street;

    @JsonProperty(required = true)
    private String number;

    private String complement;

    @JsonProperty(required = true)
    private String locality;

    @JsonProperty(required = true)
    private String city;

    @JsonProperty(value = "state", required = true)
    @JsonAlias(value = {"region_code"})
    private OrderAddressState state;

    @JsonProperty(required = true)
    @Length(min = 3, max = 3)
    private String country;

    @JsonProperty(value = "postal_code", required = true)
    private String postalCode;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public OrderAddressState getState() {
        return state;
    }

    public void setState(OrderAddressState state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", number='" + number + '\'' +
                ", complement='" + complement + '\'' +
                ", locality='" + locality + '\'' +
                ", city='" + city + '\'' +
                ", regionCode=" + state +
                ", country='" + country + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }
}
