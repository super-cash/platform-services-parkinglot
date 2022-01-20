package cash.super_.platform.model.parkinglot;

import cash.super_.platform.model.supercash.PaymentResponse;
import cash.super_.platform.model.supercash.types.charge.PaymentCharge;
import cash.super_.platform.model.supercash.types.charge.PaymentChargeResponse;
import cash.super_.platform.model.supercash.types.order.PaymentOrderResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(name = "parking_ticket_payment_date_idx", columnList = "date"),
        @Index(name = "parking_ticket_payment_user_id_idx", columnList = "user_id"),
        @Index(name = "parking_ticket_payment_service_idx", columnList = "requesterService"),
})
@JsonIncludeProperties({"id", "amount", "service_fee", "date"})
public class ParkinglotTicketPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;

    private Long date;

    @JsonProperty(value = "service_fee")
    private Long serviceFee;

    @Column(name = "user_id")
    private Long userId;

    private String requesterService;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "payment_id", foreignKey = @ForeignKey(name = "parking_ticket_payment_charge_response_fk"))
    private PaymentChargeResponse payment;

    @JsonIgnore
    @ManyToOne
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

    public PaymentChargeResponse getPayment() {
        return payment;
    }

    public void setPayment(PaymentChargeResponse paymentChargeResponse) {
        this.payment = paymentChargeResponse;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ParkinglotTicketPayment other = (ParkinglotTicketPayment) obj;
        return Objects.equal(this.parkinglotTicket.getStoreId(), other.parkinglotTicket.getStoreId())
                && Objects.equal(this.userId, other.userId)
                && Objects.equal(this.amount, other.amount)
                && Objects.equal(this.date, other.date)
                && Objects.equal(this.payment.getPaymentId(), other.payment.getPaymentId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId, this.parkinglotTicket.getStoreId(), amount, date, payment.getPaymentId());
    }
}
