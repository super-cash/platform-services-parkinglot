package cash.super_.platform.service.parkinglot.repository;

import cash.super_.platform.service.parkinglot.model.ParkingTicketState;
import cash.super_.platform.service.parkinglot.model.ParkingTicketStateTransition;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicket;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicketPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkinglotTicketStateTransitionsRepository extends JpaRepository<ParkingTicketStateTransition, Long> {

    /**
     * @param parkingTicket the parking lot
     * @return The list of all parking lot tickets ordered by the date ascending.
     */
    List<ParkingTicketStateTransition> findAllByParkinglotTicketOrderByAtAsc(ParkinglotTicket parkingTicket);

    /**
     *
     * @param parkinglotTicket
     * @param state
     * @return The parking ticket state transition at a given time
     */
    boolean existsDistinctByParkinglotTicketEqualsAndStateEquals(ParkinglotTicket parkinglotTicket, ParkingTicketState state);
}
