package cash.super_.platform.model.parkinglot;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * The relation of the parking ticket and a given state
 */
// Adding indexes https://www.baeldung.com/jpa-indexes#5-multiple-index-on-a-single-entity
@Entity
@Table(indexes = {
        @Index(name = "parking_ticket_transition_ticket_number_idx", columnList = "ticket_number"),
        @Index(name = "parking_ticket_transition_user_id_idx", columnList = "user_id"),
        @Index(name = "parking_ticket_transition_store_id_idx", columnList = "store_id"),
        @Index(name = "parking_ticket_transition_date_idx", columnList = "date"),
        @Index(name = "parking_ticket_transition_state_idx", columnList = "state"),
        @Index(name = "unique_ticket_state_transition_idx", columnList = "ticket_number, user_id, store_id, date, state", unique = true)
})
public class ParkinglotTicketStateTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumns ({
            @JoinColumn (name = "ticket_number", referencedColumnName = "ticket_number", updatable = false, insertable = false),
            @JoinColumn (name = "user_id", referencedColumnName = "user_id", updatable = false, insertable = false),
            @JoinColumn (name = "store_id", referencedColumnName = "store_id", updatable = false, insertable = false)
    })
    private ParkinglotTicket parkinglotTicket;

    /**
     * The specific time the ticket assumed this state
     */
    @NotNull
    @Column(name = "ticket_number")
    @JsonIgnore
    private Long ticketNumber;

    /**
     * The specific time the ticket assumed this state
     */
    @NotNull
    @Column(name = "user_id")
    private Long userId;

    /**
     * The specific time the ticket assumed this state
     */
    @NotNull
    @Column(name = "store_id")
    @JsonIgnore
    private Long storeId;

    /**
     * The specific time the ticket assumed this state
     */
    @NotNull
    private Long date;

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
    public static ParkinglotTicketStateTransition makeNew(ParkinglotTicket parkinglotTicket, Long userId, Long storeId, ParkingTicketState state, long dateTime) {
        ParkinglotTicketStateTransition transition = new ParkinglotTicketStateTransition();
        transition.date = dateTime;
        transition.parkinglotTicket = parkinglotTicket;
        transition.state = state;
        transition.userId = userId;
        transition.setTicketNumber(parkinglotTicket.getTicketNumber());

        // I was getting https://stackoverflow.com/questions/35356742/cant-commit-jpa-transaction-rollbackexception-transaction-marked-as-rollback/35377970
        // It is a foreign key and it was null.
        transition.storeId = storeId;
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

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(Long ticketNumber) {
        this.ticketNumber = ticketNumber;
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
                && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parkinglotTicket, date, state);
    }
}
