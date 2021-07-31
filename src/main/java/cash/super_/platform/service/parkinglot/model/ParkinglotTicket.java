package cash.super_.platform.service.parkinglot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
public class ParkinglotTicket {
    @Id
    private Long ticketNumber;

    // TODO: link the user here
//    private User user;

    // TODO: link the marketplace here
//    @OneToOne
//    @JoinColumn(name = "id")
//    private Marketplace marketplace;

    // TODO: link the store here
//    private Store store;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY,
            mappedBy = "parkinglotTicket") // mappedBy value is the name of the java class attribute there in the child class
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ParkinglotTicketPayment> payments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY,
            mappedBy = "parkinglotTicket") // mappedBy value is the name of the java class attribute there in the child class
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ParkingTicketStateTransition> states = new LinkedList<>();

    public Long getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(Long ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public List<ParkinglotTicketPayment> getPayments() {
        return payments;
    }

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
}
