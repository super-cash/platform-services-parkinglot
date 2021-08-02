package cash.super_.platform.service.payment.model.supercash.types.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Entity(name = "payment_customer_phone")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderCustomerPhone {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonProperty(required = true)
    private String country;

    @JsonProperty(required = true)
    @Length(min = 2, max = 2, message = "Invalid phone area code.")
    private String area;

    @JsonProperty(required = true)
    @Length(min = 9, max = 9, message = "Invalid phone number.")
    private String number;

    @JsonProperty(required = true)
    private String type;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private OrderCustomer customer;

    public OrderCustomerPhone() {

    }

    public OrderCustomerPhone(String country, String area, String number, String type) {
        this.country = country;
        this.area = area;
        this.number = number;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public OrderCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(OrderCustomer customer) {
        this.customer = customer;
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
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("OrderCustomerPhone{");
        sb.append(System.lineSeparator());
        sb.append("  \"id\": ").append(id).append(',').append(System.lineSeparator());
        sb.append("  \"country\": \"").append(country).append("\",").append(System.lineSeparator());
        sb.append("  \"area\": \"").append(area).append("\",").append(System.lineSeparator());
        sb.append("  \"number\": \"").append(number).append("\",").append(System.lineSeparator());
        sb.append("  \"type\": \"").append(type).append("\",").append(System.lineSeparator());
        sb.append(super.toString()).append(System.lineSeparator()).append('}');
        return sb.toString();
    }
}
