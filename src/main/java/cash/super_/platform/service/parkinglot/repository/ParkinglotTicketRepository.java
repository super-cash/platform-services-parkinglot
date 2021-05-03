package cash.super_.platform.service.parkinglot.repository;

import cash.super_.platform.service.parkinglot.model.ParkinglotTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkinglotTicketRepository extends JpaRepository<ParkinglotTicket, Long> {

    Optional<ParkinglotTicket> findByTicketNumber(Long ticketNumber);
}
