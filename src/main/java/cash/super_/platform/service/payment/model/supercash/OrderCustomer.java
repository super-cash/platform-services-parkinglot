package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "payment_customer")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private String email;

    @JsonProperty(value = "tax_id", required = true)
    @Length(min = 11, max = 14, message = "Invalid TaxId. CPF must have 11 digits or CNPJ must have 14 digits")
    private String taxId;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(name = "customer_id", foreignKey = @ForeignKey(name = "payment_customer_payment_customer_phone_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty(value = "phones")
    private List<OrderCustomerPhone> phones = new ArrayList<>();

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public List<OrderCustomerPhone> getPhones() {
        return phones;
    }

    public void setPhones(List<OrderCustomerPhone> phones) {
        this.phones = phones;
    }

    public void addPhone(String country, String area, String phone, String type) {
        this.phones.add(new OrderCustomerPhone(country, area, phone, type));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", taxId='" + taxId + '\'' +
                ", phones=" + phones +
                '}';
    }
}
