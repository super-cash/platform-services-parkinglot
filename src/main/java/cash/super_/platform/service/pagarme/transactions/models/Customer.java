package cash.super_.platform.service.pagarme.transactions.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Customer {

    @JsonProperty("external_id")
    private String externalId;

    private String name;

    private Type type;

    private String country = "br";

    private String email;

    private List<Document> documents = null;

    @JsonProperty("phone_numbers")
    private List<String> phoneNumbers = null;

    private String birthday;

    @JsonProperty("born_at")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String bornAt = null;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @Override
    public String
    toString() {
        return "Customer{" +
                "externalId='" + externalId + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", country='" + country + '\'' +
                ", email='" + email + '\'' +
                ", documents=" + documents +
                ", phoneNumbers=" + phoneNumbers +
                ", birthday='" + birthday + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return getExternalId().equals(customer.getExternalId()) && getName().equals(customer.getName()) &&
                getType() == customer.getType() && getCountry().equals(customer.getCountry()) &&
                getEmail().equals(customer.getEmail()) && getDocuments().equals(customer.getDocuments()) &&
                getPhoneNumbers().equals(customer.getPhoneNumbers()) && getBirthday().equals(customer.getBirthday());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getExternalId(), getName(), getType(), getCountry(), getEmail(), getDocuments(),
                getPhoneNumbers(), getBirthday());
    }

    public enum Type {
        @JsonProperty(value = "individual")
        INDIVIDUAL,

        @JsonProperty(value = "corporation")
        CORPORATION
    }
}
