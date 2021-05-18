package cash.super_.platform.service.payment.model.supercash.types.charge;

import cash.super_.platform.service.payment.model.supercash.card.CardResponse;
import cash.super_.platform.service.payment.model.supercash.methods.boleto.PaymentBoletoResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity(name = "payment_method_response")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChargePaymentMethodResponse extends ChargePaymentMethod {

    public String type;

    public Integer installments;

    public Boolean capture;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty(value = "card")
    public CardResponse card;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty(value = "boleto")
    public PaymentBoletoResponse boleto;

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

    public PaymentBoletoResponse getBoleto() {
        return boleto;
    }

    public void setBoleto(PaymentBoletoResponse boleto) {
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
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("ChargePaymentMethodResponse{");
        sb.append(System.lineSeparator());
        sb.append("  \"type\": \"").append(type).append("\",").append(System.lineSeparator());
        sb.append("  \"installments\": ").append(installments).append(',').append(System.lineSeparator());
        sb.append("  \"capture\": ").append(capture).append(',').append(System.lineSeparator());
        sb.append("  \"card\": ").append(card).append(',').append(System.lineSeparator());
        sb.append("  \"boleto\": ").append(boleto).append(',').append(System.lineSeparator());
        sb.append("  \"softDescriptor\": \"").append(softDescriptor).append("\",").append(System.lineSeparator());
        sb.append('}').append(System.lineSeparator());
        return sb.toString();
    }
}
