package cash.super_.platform.service.parkinglot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * The relation of the parking ticket and a given state
 */
@Entity
public class ParkingTicketStateTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="ticket_number", nullable = false, foreignKey = @ForeignKey(name = "parking_ticket_parking_ticket_state_fk"))
    private ParkinglotTicket parkinglotTicket;

    /**
     * The specific time the ticket assumed this state
     */
    private Long at;

    // https://www.baeldung.com/jpa-persisting-enums-in-jpa#string
    // Save the string value so we can reuse the value anywhere else
    /**
     * The state assumed by the ticket
     */
    @Enumerated(EnumType.STRING)
    private ParkingTicketState state;

    /**
     * Creates a new instance of the transition
     * @param parkinglotTicket
     * @param state
     * @return
     */
    public static ParkingTicketStateTransition makeNew(ParkinglotTicket parkinglotTicket, ParkingTicketState state, long time) {
        ParkingTicketStateTransition transition = new ParkingTicketStateTransition();
        transition.at = time;
        transition.parkinglotTicket = parkinglotTicket;
        transition.state = state;
        return transition;
    }

    public ParkingTicketState getState() { return state; }

    public void setState(ParkingTicketState state) { this.state = state; }

    public void setId(Long id) { this.id = id; }

    public Long getId() { return id; }

    public Long getAt() { return at; }

    public void setAt(Long at) { this.at = at; }

    public ParkinglotTicket getParkinglotTicket() { return parkinglotTicket; }

    public void setParkinglotTicket(ParkinglotTicket parkinglotTicket) { this.parkinglotTicket = parkinglotTicket; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingTicketStateTransition that = (ParkingTicketStateTransition) o;
        return id.equals(that.id) && parkinglotTicket.equals(that.parkinglotTicket) && at.equals(that.at) && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parkinglotTicket, at, state);
    }
}
