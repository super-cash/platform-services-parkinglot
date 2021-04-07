package cash.super_.platform.service.pagarme.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {

    private String country = "br";

    private String state;

    private String city;

    private String neighborhood;

    private String street;

    @JsonProperty("street_number")
    private String streetNumber;

    private String complementary = "Não possui.";

    private String zipcode;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getComplementary() {
        return complementary;
    }

    public void setComplementary(String complementary) {
        this.complementary = complementary;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    @Override
    public String toString() {
        return "Address{" +
                "country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                ", street='" + street + '\'' +
                ", streetNumber='" + streetNumber + '\'' +
                ", complementary='" + complementary + '\'' +
                ", zipcode='" + zipcode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address = (Address) o;
        return getCountry().equals(address.getCountry()) && getState().equals(address.getState()) &&
                getCity().equals(address.getCity()) && getNeighborhood().equals(address.getNeighborhood()) &&
                getStreet().equals(address.getStreet()) && getStreetNumber().equals(address.getStreetNumber()) &&
                Objects.equals(getComplementary(), address.getComplementary()) &&
                getZipcode().equals(address.getZipcode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCountry(), getState(), getCity(), getNeighborhood(), getStreet(), getStreetNumber(),
                getComplementary(), getZipcode());
    }
}
