package cash.super_.platform.repository;

import cash.super_.platform.model.parkinglot.ParkinglotTicket;
import cash.super_.platform.model.parkinglot.ParkinglotTicketId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 *  https://www.baeldung.com/spring-data-derived-queries
 *  https://stackoverflow.com/questions/14014086/what-is-difference-between-crudrepository-and-jparepository-interfaces-in-spring/14025100#14025100
 *
 *  Use EntityGraph with 1 element as EAGER and the rest as lazy.
 *  Or else will get the error https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 *  in the Entity.
 */
@Repository
public interface ParkinglotTicketRepository extends PagingAndSortingRepository<ParkinglotTicket, ParkinglotTicketId> {

    /**
     * @param ticketNumber
     * @param userId
     * @param storeId
     * @return The ticket for the current user in the current store
     */
    Optional<ParkinglotTicket> findByTicketNumberAndUserIdAndStoreId(Long ticketNumber, Long userId, Long storeId);

    /**
     * @param ticketNumber
     * @param storeId
     * @return The ticket for the current user in the current store
     */
    Optional<List<ParkinglotTicket>> findByTicketNumberAndStoreId(Long ticketNumber, Long storeId);

    /**
     * @param ticketNumber
     * @param storeId
     * @return whether the storage contains the ticket from onother user in the same store
     */
    boolean existsDistinctByTicketNumberAndStoreId(Long ticketNumber, Long storeId);

    /**
     * Adding EntityGraph to fetch all properties https://blog.ippon.tech/boost-the-performance-of-your-spring-data-jpa-application/#method1retrievingandloadingobjectswithquery
     * Getting the latest 10 tickets by default without
     *  https://stackoverflow.com/questions/24068884/spring-data-jpa-java-get-last-10-records-from-query/45334867#45334867
     * @param userId
     * @param storeId
     * @return the list of parking tickets
     */
    @EntityGraph(attributePaths = {"states", "payments"})
    Optional<List<ParkinglotTicket>> findFirst10ByUserIdAndStoreIdOrderByCreatedAtDesc(Long userId, Long storeId);

    /**
     * Adding EntityGraph to fetch all properties https://blog.ippon.tech/boost-the-performance-of-your-spring-data-jpa-application/#method1retrievingandloadingobjectswithquery
     * Pagination support https://www.programmersought.com/article/1459585922/
     * @return All the times that there's a ticket for a given user in a parking lot store
     */
    @EntityGraph(attributePaths = {"states", "payments"})
    Optional<Page<ParkinglotTicket>> findAllByUserIdAndStoreId(Long userId, Long storeId, Pageable page);

    /**
     * Adding EntityGraph to fetch all properties https://blog.ippon.tech/boost-the-performance-of-your-spring-data-jpa-application/#method1retrievingandloadingobjectswithquery
     * @param userId is the user Id
     * @param storeId is the store Id
     * @param createdAt is the base of the date
     * @param createdAtOffset is the max date
     * @return The list of parking lot between two dates.
     */
    @EntityGraph(attributePaths = {"states", "payments"})
    Optional<List<ParkinglotTicket>> findByUserIdAndStoreIdAndCreatedAtBetween(Long userId, Long storeId, Long createdAt, Long createdAtOffset);
}
