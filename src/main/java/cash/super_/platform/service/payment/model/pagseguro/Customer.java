package cash.super_.platform.service.payment.model.pagseguro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Customer {

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private String email;

    @JsonProperty(value = "tax_id", required = true)
    @Length(min = 11, max = 14, message = "Invalid TaxId. CPF must have 11 digits or CNPJ must have 14 digits")
    private String taxId;

    @JsonProperty("phones")
    private List<Phone> phones;

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

    public List<Phone> getPhoneNumbers() {
        return phones;
    }

    public void setPhoneNumbers(List<Phone> phones) {
        this.phones = phones;
    }

    public void addPhoneNumber(String country, String area, String phone, String type) {
        this.phones.add(new Phone(country, area, phone, type));
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", taxId='" + taxId + '\'' +
                ", phoneNumbers=" + phones +
                '}';
    }
}
