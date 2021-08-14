package cash.super_.platform.service.parkinglot.repository;

import cash.super_.platform.model.parkinglot.ParkinglotTicket;
import cash.super_.platform.model.parkinglot.ParkinglotTicketPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkinglotTicketPaymentsRepository extends JpaRepository<ParkinglotTicketPayment, Long> {

//    @Query("SELECT ptp FROM ParkinglotTicket pt JOIN ParkinglotTicketPayment ptp JOIN TransactionResponse t " +
//            "WHERE (pt.ticketNumber = :ticketNumber AND ptp.transactionResponse.transactionId = t.transactionId " +
//            "AND t.status = :status)")
//    List<ParkinglotTicketPayment> findByTicketNumberAndTransactionStatusOrderByPayments(
//            @Param("ticketNumber") Long ticketNumber, @Param("status") Transaction.Status status);

//    @Query("SELECT ptp FROM ParkinglotTicket pt JOIN ParkinglotTicketPayment ptp " +
//            "WHERE (pt.ticketNumber = :ticketNumber AND ptp.date = :date)")
//    Optional<ParkinglotTicketPayment> findByTicketNumberAndDate(@Param("ticketNumber") Long ticketNumber,
//                                                                @Param("date") Long date);
    Optional<ParkinglotTicketPayment> findByDateAndParkinglotTicket(Long date, ParkinglotTicket parkinglotTicket);
}
