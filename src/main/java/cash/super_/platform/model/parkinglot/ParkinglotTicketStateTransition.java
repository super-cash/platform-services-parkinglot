package cash.super_.platform.model.parkinglot;

import cash.super_.platform.util.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Random;

/**
 * The relation of the parking ticket and a given state
 */
// Adding indexes https://www.baeldung.com/jpa-indexes#5-multiple-index-on-a-single-entity
@Entity
@Table(indexes = {
        @Index(name = "parking_ticket_transition_user_id_idx", columnList = "user_id"),
        @Index(name = "parking_ticket_transition_date_idx", columnList = "date"),
        @Index(name = "parking_ticket_transition_date_idx", columnList = "entryDate"),
        @Index(name = "parking_ticket_transition_state_idx", columnList = "state"),
        @Index(name = "unique_ticket_state_transition_idx", columnList = "user_id, date, state", unique = true)
})
public class ParkinglotTicketStateTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @JsonIgnore
    @ManyToOne
    private ParkinglotTicket parkinglotTicket;

    /**
     * The specific user_id the ticket assumed this state
     */
    @NotNull
    @Column(name = "user_id")
    private Long userId;

    /**
     * The specific time the ticket assumed this state
     */
    @NotNull
    private Long date;

    /**
     * The specific time this state was added in the database (it is used, for example, to get the latest state)
     */
    @NotNull
    private Long entryDate;

    // https://www.baeldung.com/jpa-persisting-enums-in-jpa#string
    // Save the string value so we can reuse the value anywhere else
    /**
     * The state assumed by the ticket
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private ParkingTicketState state;

    /**
     * Creates a new instance of the transition
     * @param parkinglotTicket
     * @param state
     * @return
     */
    public static ParkinglotTicketStateTransition makeNew(ParkinglotTicket parkinglotTicket, Long userId,
                                                          ParkingTicketState state, long dateTime) {
        ParkinglotTicketStateTransition transition = new ParkinglotTicketStateTransition();
        transition.date = dateTime;
        transition.parkinglotTicket = parkinglotTicket;
        transition.state = state;
        transition.userId = userId;
        transition.entryDate = DateTimeUtil.getNow() + 1;

        return transition;
    }

    public ParkingTicketState getState() {
        return state;
    }

    public void setState(ParkingTicketState state) {
        this.state = state;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Long entryDate) {
        this.entryDate = entryDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ParkinglotTicket getParkinglotTicket() {
        return parkinglotTicket;
    }

    public void setParkinglotTicket(ParkinglotTicket parkinglotTicket) {
        this.parkinglotTicket = parkinglotTicket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkinglotTicketStateTransition that = (ParkinglotTicketStateTransition) o;
        return parkinglotTicket.equals(that.parkinglotTicket)
                && date.equals(that.date)
                && entryDate.equals(that.entryDate)
                && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parkinglotTicket, date, entryDate, state);
    }
}
