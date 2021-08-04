package cash.super_.platform.service.parkinglot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

// Adding indexes https://www.baeldung.com/jpa-indexes#5-multiple-index-on-a-single-entity
@Entity
@Table(indexes = {
        @Index(name = "user_id_idx", columnList = "userId"),
        @Index(name = "created_at_idx", columnList = "createdAt"),
        @Index(name = "store_id_idx", columnList = "storeId"),
        @Index(name = "unique_ticket_idx", columnList = "ticketNumber, userId, storeId", unique = true)
})
public class ParkinglotTicket {

    // TODO: Ticket numbers must change to String as they will differ from each parkinglot
    @Id
    private Long ticketNumber;

    // TODO: Map to the User bean after we do the shared libraries
    @NotNull
    private Long userId;

    // TODO: Map to the User bean after we do the shared libraries
    @NotNull
    private Long storeId;

    @NotNull
    private Long createdAt;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY,
            mappedBy = "parkinglotTicket") // mappedBy value is the name of the java class attribute there in the child class
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ParkinglotTicketPayment> payments = new HashSet<>();

    // Just one element as EAGER or else it returns a multiple bags exception
    // https://vladmihalcea.com/hibernate-multiplebagfetchexception/
    // Also, using Set instead of List for queries is appropriate model
    // since they are unique based on their IDs
    // https://vladmihalcea.com/hibernate-multiplebagfetchexception/
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER,
            mappedBy = "parkinglotTicket") // mappedBy value is the name of the java class attribute there in the child class
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ParkingTicketStateTransition> states = new HashSet<>();

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public Long getTicketNumber() { return ticketNumber; }

    public void setTicketNumber(Long ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Set<ParkinglotTicketPayment> getPayments() { return payments; }

    public void setPayments(Set<ParkinglotTicketPayment> payments) {
        this.payments = payments;
    }

    public void addPayment(ParkinglotTicketPayment parkinglotTicketPayment) {
        this.payments.add(parkinglotTicketPayment);
    }

    public Set<ParkingTicketStateTransition> getStates() {
        return states;
    }

    public void setStates(Set<ParkingTicketStateTransition> states) {
        this.states = states;
    }

    public void addTicketStateTransition(ParkingTicketState state, long time) {
        ParkingTicketStateTransition transition = ParkingTicketStateTransition.makeNew(this, state, time);
        this.states.add(transition);
    }

    @JsonIgnore
    public ParkingTicketStateTransition getLastStateRecorded() {
        Deque<ParkingTicketStateTransition> sortedTicketStates = new LinkedList<>(this.getStates());
        ParkingTicketStateTransition lastStateRecorded = sortedTicketStates.getLast();
        return lastStateRecorded;
    }

    @JsonIgnore
    public long getLastPaymentDateTimeMillis() {
        return this.getPayments().stream().mapToLong(p -> p.getDate()).sorted().max().getAsLong();
    }
}
