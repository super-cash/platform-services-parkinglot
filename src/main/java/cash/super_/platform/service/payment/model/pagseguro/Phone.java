package cash.super_.platform.service.payment.model.pagseguro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Phone {

    @JsonProperty(required = true)
    private String country;

    @JsonProperty(required = true)
    @Length(min = 2, max = 2, message = "Invalid phone area code.")
    public String area;

    @JsonProperty(required = true)
    @Length(min = 9, max = 9, message = "Invalid phone number.")
    public String number;

    @JsonProperty(required = true)
    public String type;

    public Phone() {

    }

    public Phone(String country, String area, String number, String type) {
        this.country = country;
        this.area = area;
        this.number = number;
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "PhoneNumber{" +
                "country='" + country + '\'' +
                ", area='" + area + '\'' +
                ", number='" + number + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
