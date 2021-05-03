package cash.super_.platform.service.payment.model.pagseguro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Holder {

    @JsonProperty(required = true)
    private String name;

    public Holder() { }

    public Holder(String name) { this.name = name; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Holder{" +
                "name='" + name + '\'' +
                '}';
    }
}
