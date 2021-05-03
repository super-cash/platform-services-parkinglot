package cash.super_.platform.service.payment.model;

import cash.super_.platform.service.payment.model.pagarme.Transaction;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponseSummary {

    /**
     * Supercash transaction ID in the database.
     */
    @JsonProperty(value = "transaction_id")
    private String transactionId;

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
    private Transaction.Status status;

    /**
     * Número identificador da transação no gateway
     */
    @JsonProperty(value = "gateway_transaction_id")
    private Integer gatewayTransactionId;

    /**
     * Objeto com dados adicionais do cliente/produto/serviço vendido
     */
    private Map<String, String> metadata = new HashMap<>();

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
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

    public Transaction.Status getStatus() {
        return status;
    }

    public void setStatus(Transaction.Status status) {
        this.status = status;
    }

    public Integer getGatewayTransactionId() {
        return gatewayTransactionId;
    }

    public void setGatewayTransactionId(Integer gatewayTransactionId) {
        this.gatewayTransactionId = gatewayTransactionId;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "TransactionResponseSummary{" +
                "transactionId=" + transactionId +
                ", paidAmount=" + paidAmount +
                ", uuid=" + uuid +
                ", status=" + status +
                ", gatewayTransactionId=" + gatewayTransactionId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionResponseSummary)) return false;
        TransactionResponseSummary that = (TransactionResponseSummary) o;
        return getTransactionId().equals(that.getTransactionId()) && getUuid().equals(that.getUuid()) &&
                getGatewayTransactionId().equals(that.getGatewayTransactionId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTransactionId(), getUuid(), getGatewayTransactionId());
    }
}
