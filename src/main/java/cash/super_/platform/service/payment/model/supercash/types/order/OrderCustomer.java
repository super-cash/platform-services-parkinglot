package cash.super_.platform.service.payment.model.supercash.types.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
    private Set<OrderCustomerPhone> phones = new HashSet<>();

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public Set<OrderCustomerPhone> getPhones() {
        return phones;
    }

    public void setPhones(Set<OrderCustomerPhone> phones) {
        this.phones = phones;
    }

    public void addPhone(String country, String area, String phone, String type) {
        this.phones.add(new OrderCustomerPhone(country, area, phone, type));
    }

    public void addPhone(OrderCustomerPhone phone) {
        this.phones.add(phone);
    }

    public void clearPhones() {
        this.phones.clear();
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
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("OrderCustomer{");
        sb.append(System.lineSeparator());
        sb.append("  \"id\": ").append(id).append(',').append(System.lineSeparator());
        sb.append("  \"name\": \"").append(name).append("\",").append(System.lineSeparator());
        sb.append("  \"email\": \"").append(email).append("\",").append(System.lineSeparator());
        sb.append("  \"taxId\": \"").append(taxId).append("\",").append(System.lineSeparator());
        sb.append("  \"phones\": ").append(phones).append(',').append(System.lineSeparator());
        sb.append(super.toString()).append(System.lineSeparator()).append('}');
        return sb.toString();
    }
}
