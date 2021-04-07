package cash.super_.platform.service.pagarme.model;

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

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getPaidAmount() {
        return paidAmount;
    }

    public String getUuid() {
        return uuid;
    }

    public Transaction.Status getStatus() {
        return status;
    }

    public Integer getGatewayTransactionId() {
        return gatewayTransactionId;
    }

    public Map<String, String> getMetadata() {
        return metadata;
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
