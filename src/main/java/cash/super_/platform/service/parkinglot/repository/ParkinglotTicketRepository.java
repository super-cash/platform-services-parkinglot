package cash.super_.platform.service.parkinglot.repository;

import cash.super_.platform.service.pagarme.transactions.models.Transaction;
import cash.super_.platform.service.parkinglot.model.Marketplace;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicket;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicketPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkinglotTicketRepository extends JpaRepository<ParkinglotTicket, Long> {

    Optional<ParkinglotTicket> findByTicketNumber(Long ticketNumber);
}
