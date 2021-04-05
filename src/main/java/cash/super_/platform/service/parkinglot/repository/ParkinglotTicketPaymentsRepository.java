package cash.super_.platform.service.parkinglot.repository;

import cash.super_.platform.service.pagarme.transactions.models.Transaction;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicket;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicketPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

@Repository
public interface ParkinglotTicketPaymentsRepository extends JpaRepository<ParkinglotTicketPayment, Long> {

    @Query("SELECT ptp FROM ParkinglotTicket pt JOIN ParkinglotTicketPayment ptp " +
            "WHERE (pt.ticketNumber = :ticketNumber AND ptp.status = :status)")
    List<ParkinglotTicketPayment> findByTicketNumberAndTransactionStatusOrderByPayments(
            @Param("ticketNumber") Long ticketNumber, @Param("status") Transaction.Status status);

//    @Query("SELECT ptp FROM ParkinglotTicket pt JOIN ParkinglotTicketPayment ptp " +
//            "WHERE (pt.ticketNumber = :ticketNumber AND ptp.date = :date)")
//    Optional<ParkinglotTicketPayment> findByTicketNumberAndDate(@Param("ticketNumber") Long ticketNumber,
//                                                                @Param("date") Long date);
    Optional<ParkinglotTicketPayment> findByDateAndParkinglotTicket(Long date, ParkinglotTicket parkinglotTicket);
}
