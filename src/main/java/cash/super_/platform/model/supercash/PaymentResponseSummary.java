package cash.super_.platform.model.supercash;

import cash.super_.platform.model.supercash.types.charge.ChargeStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponseSummary {

    /**
     * Supercash transaction ID in the database.
     */
    @JsonProperty(value = "transaction_id")
    private Long transactionId;

    /**
     * Paid amount in the transaction
     */
    @JsonProperty(value = "paid_amount")
    private Long paidAmount;

    /**
     * Supercash internal ID, defined when a request enter for payment.
     */
    @JsonProperty(value = "uuid")
    private String uuid;

    /**
     * Para cada atualização no processamento da transação, esta propriedade
     * será alterada, e o objeto <code>transaction</code> retornado como
     * resposta através da sua URL de postback ou após o término do
     * processamento da ação atual.
     */
    private ChargeStatus status;

    /**
     * Gateway usado para processar o pagamento
     */
    @JsonProperty(value = "gateway")
    private PaymentGateway gateway;

    /**
     * Objeto com dados adicionais do cliente/produto/serviço vendido
     */
    private Map<String, String> metadata = new HashMap<>();

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Long paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ChargeStatus getStatus() {
        return status;
    }

    public void setStatus(ChargeStatus status) {
        this.status = status;
    }

    public PaymentGateway getGateway() {
        return gateway;
    }

    public void setGateway(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("PaymentResponseSummary{");
        sb.append(System.lineSeparator());
        sb.append("  \"transactionId\": ").append(transactionId).append(',').append(System.lineSeparator());
        sb.append("  \"paidAmount\": ").append(paidAmount).append(',').append(System.lineSeparator());
        sb.append("  \"uuid\": \"").append(uuid).append("\",").append(System.lineSeparator());
        sb.append("  \"status\": ").append(status).append(',').append(System.lineSeparator());
        sb.append("  \"gateway\": ").append(gateway).append(',').append(System.lineSeparator());
        sb.append("  \"metadata\": ").append(metadata).append(',').append(System.lineSeparator());
        sb.append(super.toString()).append(System.lineSeparator()).append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentResponseSummary)) return false;
        PaymentResponseSummary that = (PaymentResponseSummary) o;
        return getTransactionId().equals(that.getTransactionId()) && getUuid().equals(that.getUuid()) &&
                getGateway().equals(that.getGateway());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTransactionId(), getUuid(), getGateway());
    }
}
