package cash.super_.platform.service.parkinglot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.Comparator;

// Adding indexes https://www.baeldung.com/jpa-indexes#5-multiple-index-on-a-single-entity
@Entity
@Table(indexes = {
        @Index(name = "ticket_number_idx", columnList = "ticket_number"),
        @Index(name = "user_id_idx", columnList = "user_id"),
        @Index(name = "store_id_idx", columnList = "store_id"),
        @Index(name = "created_at_idx", columnList = "createdAt"),
        @Index(name = "unique_ticket_idx", columnList = "ticket_number, user_id, store_id", unique = true)
})

// // https://www.baeldung.com/jpa-composite-primary-keys#idclass
// Adding the id class
@IdClass(ParkinglotTicketId.class)
public class ParkinglotTicket {

    // TODO: Ticket numbers must change to String as they will differ from each parkinglot
    @Id
    @Column(name = "ticket_number")
    private Long ticketNumber;

    // TODO: Map to the User bean after we do the shared libraries
    @Id
    @Column(name = "user_id")
    private Long userId;

    // TODO: Map to the User bean after we do the shared libraries
    @Id
    @Column(name = "store_id")
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
    private Set<ParkinglotTicketStateTransition> states = new HashSet<>();

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

    public Set<ParkinglotTicketPayment> getPayments() {
        return payments;
    }

    public void setPayments(Set<ParkinglotTicketPayment> payments) {
        this.payments = payments;
    }

    public void addPayment(ParkinglotTicketPayment parkinglotTicketPayment) {
        this.payments.add(parkinglotTicketPayment);
    }

    public Set<ParkinglotTicketStateTransition> getStates() {
        return states;
    }

    public void setStates(Set<ParkinglotTicketStateTransition> states) {
        this.states = states;
    }

    public void addTicketStateTransition(ParkingTicketState state, Long userId, Long storeId, long time) {
        ParkinglotTicketStateTransition transition = ParkinglotTicketStateTransition.makeNew(this, userId, storeId, state, time);

        // tickets can be only picked up and in grace period only once
        // Tickets can be scanned multiple times by the same or different users
        boolean inInitState = EnumSet.of(ParkingTicketState.PICKED_UP, ParkingTicketState.GRACE_PERIOD).contains(transition.getState());
        if (!inInitState || !this.contains(state)) {
            this.states.add(transition);
        }
    }

    public boolean contains(ParkingTicketState stateToFind) {
        Optional<ParkinglotTicketStateTransition> findResult = this.states.stream()
                .filter(state -> state.getState() == stateToFind)
                .findFirst();
        return findResult.isPresent();
    }

    public static Set<ParkingTicketState> lastRecordedExclusionType() {
      return EnumSet.of(ParkingTicketState.PICKED_UP, ParkingTicketState.SCANNED);
    }

    @JsonIgnore
    public ParkinglotTicketStateTransition getLastStateRecorded() {
        if (this.states == null || this.states.isEmpty()) {
            return null;
        }
        Optional<ParkinglotTicketStateTransition> firstElement = this.states.stream()
                // exclude the states that the ticket gets in the start when they are registered
                .filter(transition -> !lastRecordedExclusionType().contains(transition.getState()))
                // https://stackoverflow.com/questions/26568555/sorting-by-property-in-java-8-stream/26568724#26568724
                .sorted(Comparator.comparing(ParkinglotTicketStateTransition::getDate)
                        // https://stackoverflow.com/questions/28607191/how-to-use-a-java8-lambda-to-sort-a-stream-in-reverse-order/62208724#62208724
                        .reversed())
                .findFirst();
        return firstElement.isPresent() ? firstElement.get() : null;
    }

    @JsonIgnore
    public long getLastPaymentDateTimeMillis() {
        if (this.getPayments().isEmpty()) {
            return 0;
        }
        return this.getPayments().stream().mapToLong(p -> p.getDate()).sorted().max().getAsLong();
    }
}
