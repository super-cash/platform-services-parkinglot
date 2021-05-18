package cash.super_.platform.service.parkinglot.model;

import cash.super_.platform.service.payment.model.supercash.types.order.PaymentOrderResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    private Long date;

    private Long marketplaceId;

    private Long storeId;

    private String requesterService;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "payment_id", foreignKey = @ForeignKey(name = "parking_ticket_payment_order_response_fk"))
    private PaymentOrderResponse payment;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="ticket_number", nullable = false, foreignKey = @ForeignKey(name = "parking_ticket_parking_ticket_payment_fk"))
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

    public ParkinglotTicket getParkinglotTicket() {
        return parkinglotTicket;
    }

    public void setParkinglotTicket(ParkinglotTicket parkinglotTicket) {
        this.parkinglotTicket = parkinglotTicket;
    }

    public PaymentOrderResponse getPayment() {
        return payment;
    }

    public void setPayment(PaymentOrderResponse paymentOrderResponse) {
        this.payment = paymentOrderResponse;
    }
}
