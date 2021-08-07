package cash.super_.platform.service.parkinglot.model;

import cash.super_.platform.service.payment.model.supercash.types.order.PaymentOrderResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(name = "parking_ticket_payment_amount_idx", columnList = "amount"),
        @Index(name = "parking_ticket_payment_date_idx", columnList = "date"),
        @Index(name = "parking_ticket_payment_service_idx", columnList = "requesterService"),
        @Index(name = "parking_ticket_payment_ticket_number_idx", columnList = "ticket_number"),
        @Index(name = "parking_ticket_payment_user_id_idx", columnList = "userId"),
        @Index(name = "parking_ticket_payment_store_id_idx", columnList = "storeId"),
        @Index(name = "unique_ticket_payment_idx", columnList = "ticket_number, date, storeId, userId, amount", unique = true)
})
@JsonIncludeProperties({"id", "amount", "service_fee", "date"})
public class ParkinglotTicketPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long amount;

    @JsonProperty(value = "service_fee")
    private Long serviceFee;

    private Long date;

    private Long storeId;

    private Long userId;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ParkinglotTicketPayment other = (ParkinglotTicketPayment) obj;
        return Objects.equal(this.storeId, other.storeId)
                && Objects.equal(this.userId, other.userId)
                && Objects.equal(this.amount, other.amount)
                && Objects.equal(this.date, other.date)
                && Objects.equal(this.payment, other.payment);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId, storeId, amount, date, payment);
    }
}
