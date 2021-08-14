package cash.super_.platform.model.supercash.types.charge;

import cash.super_.platform.model.supercash.card.CardRequest;
import cash.super_.platform.model.supercash.methods.boleto.PaymentBoletoRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Min;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChargePaymentMethodRequest extends ChargePaymentMethod {

    private static final Logger LOG = LoggerFactory.getLogger(ChargePaymentMethodRequest.class);

    @JsonProperty(value = "type")
    private ChargePaymentMethodType type;

    @Min(value = 2)
    private Integer installments;

    private Boolean capture;

    @JsonProperty(value = "soft_descriptor")
    @Length(max = 17)
    private String softDescriptor;

    @JsonProperty(value = "card")
    private CardRequest card;

    @JsonProperty(value = "boleto")
    private PaymentBoletoRequest boleto;

    public ChargePaymentMethodType getType() {
        return type;
    }

    public void setType(ChargePaymentMethodType type) {
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

    public String getSoftDescriptor() {
        return softDescriptor;
    }

    public void setSoftDescriptor(String softDescriptor) {
        this.softDescriptor = softDescriptor;
    }

    public CardRequest getCard() {
        return card;
    }

    public void setCard(CardRequest card) {
        this.card = card;
    }

    public PaymentBoletoRequest getBoleto() {
        return boleto;
    }

    public void setBoleto(PaymentBoletoRequest boleto) {
        this.boleto = boleto;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("ChargePaymentMethodRequest{");
        sb.append(System.lineSeparator());
        sb.append("  \"type\": ").append(type).append(',').append(System.lineSeparator());
        sb.append("  \"installments\": ").append(installments).append(',').append(System.lineSeparator());
        sb.append("  \"capture\": ").append(capture).append(',').append(System.lineSeparator());
        sb.append("  \"softDescriptor\": \"").append(softDescriptor).append("\",").append(System.lineSeparator());
        sb.append("  \"card\": ").append(card).append(',').append(System.lineSeparator());
        sb.append("  \"boleto\": ").append(boleto).append(',').append(System.lineSeparator());
        sb.append('}').append(System.lineSeparator());
        return sb.toString();
    }
}
