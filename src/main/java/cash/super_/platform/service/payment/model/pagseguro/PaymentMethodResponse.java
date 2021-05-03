package cash.super_.platform.service.payment.model.pagseguro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentMethodResponse {

    public String type;

    public Integer installments;

    public Boolean capture;

    @JsonProperty(value = "card")
    public CardResponse card;

    @JsonProperty(value = "boleto")
    public BoletoResponse boleto;

    @JsonProperty(value = "soft_descriptor")
    public String softDescriptor;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Boolean getCapture() {
        return capture;
    }

    public void setCapture(Boolean capture) {
        this.capture = capture;
    }

    public CardResponse getCard() {
        return card;
    }

    public void setCard(CardResponse card) {
        this.card = card;
    }

    public BoletoResponse getBoleto() {
        return boleto;
    }

    public void setBoleto(BoletoResponse boleto) {
        this.boleto = boleto;
    }

    public String getSoftDescriptor() {
        return softDescriptor;
    }

    public void setSoftDescriptor(String softDescriptor) {
        this.softDescriptor = softDescriptor;
    }

    @Override
    public String toString() {
        return "PaymentMethodResponse{" +
                "type='" + type + '\'' +
                ", installments=" + installments +
                ", capture=" + capture +
                ", card=" + card +
                ", boleto=" + boleto +
                ", softDescriptor='" + softDescriptor + '\'' +
                '}';
    }
}
