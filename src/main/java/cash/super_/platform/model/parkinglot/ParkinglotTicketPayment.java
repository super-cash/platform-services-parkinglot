package cash.super_.platform.model.parkinglot;

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
        @Index(name = "parking_ticket_payment_amount_idx", columnList = "amount"),
        @Index(name = "parking_ticket_payment_date_idx", columnList = "date"),
        @Index(name = "parking_ticket_payment_service_idx", columnList = "requesterService"),
        @Index(name = "parking_ticket_payment_user_id_idx", columnList = "user_id"),
        @Index(name = "parking_ticket_payment_store_id_idx", columnList = "store_id"),
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

    // Need to annotate all columns
    // Caused by: org.hibernate.DuplicateMappingException: Table [parkinglot_ticket_payment] contains physical column name [user_id] referred to by multiple logical column names: [user_id], [userId]
    @Column(name = "store_id")
    private Long storeId;

    // https://stackoverflow.com/questions/57691377/a-column-in-a-table-is-referred-to-by-multiple-physical-column-names
    @Column(name = "user_id")
    private Long userId;

    private String requesterService;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "payment_id", foreignKey = @ForeignKey(name = "parking_ticket_payment_order_response_fk"))
    private PaymentOrderResponse payment;

    @JsonIgnore
    @ManyToOne
    @JoinColumns ({
            // Caused by: org.hibernate.MappingException: Repeated column in mapping for entity:
            // cash.super_.platform.model.parkinglot.ParkinglotTicketStateTransition column: store_id
            // (should be mapped with insert="false" update="false")
            @JoinColumn (name = "ticket_number", referencedColumnName = "ticket_number", updatable = false, insertable = false),
            @JoinColumn (name = "user_id", referencedColumnName = "user_id", updatable = false, insertable = false),
            @JoinColumn (name = "store_id", referencedColumnName = "store_id", updatable = false, insertable = false)
    })
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
