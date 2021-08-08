package cash.super_.platform.service.parkinglot.repository;

import cash.super_.platform.service.parkinglot.model.ParkingTicketState;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicketStateTransition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ParkinglotTicketStateTransitionsRepository extends JpaRepository<ParkinglotTicketStateTransition, Long> {

    Optional<List<ParkinglotTicketStateTransition>> findFirst1ByParkinglotTicket_TicketNumberAndStateNotInOrderByDateDesc(
            Long ticketNumber, Set<ParkingTicketState> notInStates);
}