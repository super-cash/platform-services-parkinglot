package cash.super_.platform.service.parkinglot.repository;

import cash.super_.platform.service.parkinglot.model.ParkinglotTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//https://stackoverflow.com/questions/14014086/what-is-difference-between-crudrepository-and-jparepository-interfaces-in-spring/14025100#14025100
@Repository
public interface ParkinglotTicketRepository extends JpaRepository<ParkinglotTicket, Long> {

    Optional<ParkinglotTicket> findByTicketNumber(Long ticketNumber);

    Optional<List<ParkinglotTicket>> findByUserId(Long userId);

    // https://www.baeldung.com/spring-data-derived-queries
    List<ParkinglotTicket> findByUserIdInAndCreatedAtIn(List<Long> userId, List<Long> createdAt);

    Optional<List<ParkinglotTicket>> findByUserIdAndCreatedAtIsGreaterThanEqualAndCreatedAtIsLessThanEqual(Long userId, Long createdAt, Long createdAtOffset);
}
