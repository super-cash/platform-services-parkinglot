package cash.super_.platform.service.parkinglot.repository;

import cash.super_.platform.service.parkinglot.model.ParkinglotTicketStateTransition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkinglotTicketStateTransitionsRepository extends JpaRepository<ParkinglotTicketStateTransition, Long> {

}
