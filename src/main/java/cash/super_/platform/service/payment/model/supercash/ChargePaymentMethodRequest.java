package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonProperty(value = "encryptedCard")
    private String encryptedCard;

    @JsonProperty(value = "boleto")
    private BoletoRequest boleto;

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

    public void setEncryptedCard(String encryptedCard) {
        this.encryptedCard = encryptedCard;
    }

    @JsonIgnore
    public String getEncryptedCard() {
        return encryptedCard;
    }

    public BoletoRequest getBoleto() {
        return boleto;
    }

    public void setBoleto(BoletoRequest boleto) {
        this.boleto = boleto;
    }

    @Override
    public String toString() {
        return "PaymentMethodRequest{" +
                "type=" + type +
                ", installments=" + installments +
                ", capture=" + capture +
                ", softDescriptor='" + softDescriptor + '\'' +
                ", card=" + card +
                ", boleto=" + boleto +
                '}';
    }
}
