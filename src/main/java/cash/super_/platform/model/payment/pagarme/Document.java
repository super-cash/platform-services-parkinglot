package cash.super_.platform.model.payment.pagarme;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Document {

    private Type type;

    private String number;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Document{" +
                "type='" + type + '\'' +
                ", number='" + number + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;
        Document document = (Document) o;
        return getType() == document.getType() && getNumber().equals(document.getNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getNumber());
    }

    public enum Type {
        @JsonProperty(value = "cpf")
        CPF,

        @JsonProperty(value = "cnpj")
        CNPJ
    }
}
