package cash.super_.platform.service.parkinglot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

@Entity
public class ParkinglotTicket {

    @Id
    private Long ticketNumber;

    // TODO: Map to the User bean after we do the shared libraries
    @NotNull
    private Long userId;

    @NotNull
    private Long createdAt;

//    @JsonIgnore
//    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY,
//            mappedBy = "parkinglotTicket") // mappedBy value is the name of the java class attribute there in the child class
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    private Marketplace marketplace;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY,
            mappedBy = "parkinglotTicket") // mappedBy value is the name of the java class attribute there in the child class
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ParkinglotTicketPayment> payments = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY,
            mappedBy = "parkinglotTicket") // mappedBy value is the name of the java class attribute there in the child class
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ParkingTicketStateTransition> states = new LinkedList<>();

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

    public List<ParkinglotTicketPayment> getPayments() { return payments; }

    public void setPayments(List<ParkinglotTicketPayment> payments) {
        this.payments = payments;
    }

    public void addPayment(ParkinglotTicketPayment parkinglotTicketPayment) {
        this.payments.add(parkinglotTicketPayment);
    }

    public List<ParkingTicketStateTransition> getStates() {
        return states;
    }

    public void setStates(List<ParkingTicketStateTransition> states) {
        this.states = states;
    }

    public void addTicketStateTransition(ParkingTicketState state, long time) {
        ParkingTicketStateTransition transition = ParkingTicketStateTransition.makeNew(this, state, time);
        this.states.add(transition);
    }

    public ParkingTicketStateTransition getLastStateRecorded() {
        Deque<ParkingTicketStateTransition> sortedTicketStates = new LinkedList<>(this.getStates());
        ParkingTicketStateTransition lastStateRecorded = sortedTicketStates.getLast();
        return lastStateRecorded;
    }

    public long getLastPaymentDateTimeMillis() {
        return this.getPayments().stream().mapToLong(p -> p.getDate()).sorted().max().getAsLong();
    }
}
