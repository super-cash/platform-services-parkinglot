package cash.super_.platform.model.parkinglot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.Comparator;

// Adding indexes https://www.baeldung.com/jpa-indexes#5-multiple-index-on-a-single-entity
// // https://www.baeldung.com/jpa-composite-primary-keys#idclass
// Adding the id class
@Entity
@Table(indexes = {
        @Index(name = "ticket_number_idx", columnList = "ticket_number, store_id", unique = true),
        @Index(name = "created_at_idx", columnList = "createdAt")
})
public class ParkinglotTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: Ticket numbers must change to String as they will differ from each parkinglot
    @Column(name = "ticket_number")
    private Long ticketNumber;

    // TODO: Map to the User bean after we do the shared libraries
    @Column(name = "store_id")
    private Long storeId;

    @NotNull
    private Long createdAt;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER,
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
    private List<ParkinglotTicketStateTransition> states = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<ParkinglotTicketStateTransition> getStates() {
        return states;
    }

    public void setStates(List<ParkinglotTicketStateTransition> states) {
        this.states = states;
    }

    public ParkinglotTicketStateTransition addTicketStateTransition(ParkingTicketState state, Long userId, long time) {
        ParkinglotTicketStateTransition transition = ParkinglotTicketStateTransition.makeNew(this, userId,
                state, time);
        // tickets can be only picked up and in grace period only once
        // Tickets can be scanned multiple times by the same or different users
        boolean inInitState = EnumSet.of(ParkingTicketState.PICKED_UP, ParkingTicketState.GRACE_PERIOD).contains(transition.getState());
        if (!inInitState || !this.contains(state)) {
            this.states.add(transition);
        }
        return transition;
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
        // exclude the states that the ticket gets in the start when they are registered
        // https://stackoverflow.com/questions/26568555/sorting-by-property-in-java-8-stream/26568724#26568724
        // https://stackoverflow.com/questions/28607191/how-to-use-a-java8-lambda-to-sort-a-stream-in-reverse-order/62208724#62208724
//        Optional<ParkinglotTicketStateTransition> firstElement = this.states.stream()
//                // exclude the states that the ticket gets in the start when they are registered
//                .filter(transition -> !lastRecordedExclusionType().contains(transition.getState()))
//                // https://stackoverflow.com/questions/26568555/sorting-by-property-in-java-8-stream/26568724#26568724
//                .sorted(Comparator.comparing(ParkinglotTicketStateTransition::getEntryDate)
//                        // https://stackoverflow.com/questions/28607191/how-to-use-a-java8-lambda-to-sort-a-stream-in-reverse-order/62208724#62208724
//                        .reversed())
//                .findFirst();

        Optional<ParkinglotTicketStateTransition> firstElement = this.states.stream()
                .filter(transition -> !lastRecordedExclusionType().contains(transition.getState())).max(Comparator.comparing(ParkinglotTicketStateTransition::getEntryDate));

        return firstElement.orElse(null);
    }

    @JsonIgnore
    public long getLastPaymentDateTimeMillis() {
        if (this.getPayments().isEmpty()) {
            return 0;
        }
        return this.getPayments().stream().mapToLong(p -> p.getDate()).sorted().max().getAsLong();
    }

    @Override
    public String toString() {
        return "ParkinglotTicket{" +
                "id=" + id +
                ", ticketNumber=" + ticketNumber +
                ", storeId=" + storeId +
                ", createdAt=" + createdAt +
                ", payments=" + payments +
                ", states=" + states +
                '}';
    }
}
