package cash.super_.platform.service.parkinglot.model;

import cash.super_.platform.service.pagarme.transactions.models.Transaction;
import cash.super_.platform.service.pagarme.transactions.models.TransactionResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
public class ParkinglotTicketPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long amount;

    @JsonProperty(value = "service_fee")
    private Long serviceFee;

    private Transaction.Status status;

    private Long date;

    private Long marketplaceId;

    private Long storeId;

    private String requesterService;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "transaction_id")
    private TransactionResponse transactionResponse;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ticket_number")
    private ParkinglotTicket parkinglotTicket;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Long serviceFee) {
        this.serviceFee = serviceFee;
    }

    public Transaction.Status getStatus() {
        return status;
    }

    public void setStatus(Transaction.Status status) {
        this.status = status;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getMarketplaceId() {
        return marketplaceId;
    }

    public void setMarketplaceId(Long marketplaceId) {
        this.marketplaceId = marketplaceId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getRequesterService() {
        return requesterService;
    }

    public void setRequesterService(String requesterService) {
        this.requesterService = requesterService;
    }

    public TransactionResponse getTransactionResponse() {
        return transactionResponse;
    }

    public void setTransactionResponse(TransactionResponse transactionResponse) {
        this.transactionResponse = transactionResponse;
    }
}
