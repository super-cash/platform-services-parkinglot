package cash.super_.platform.service.parkinglot.repository;

import cash.super_.platform.service.parkinglot.model.ParkinglotTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 *  https://www.baeldung.com/spring-data-derived-queries
 *  https://stackoverflow.com/questions/14014086/what-is-difference-between-crudrepository-and-jparepository-interfaces-in-spring/14025100#14025100
 */
@Repository
public interface ParkinglotTicketRepository extends PagingAndSortingRepository<ParkinglotTicket, Long> {

    Optional<ParkinglotTicket> findByTicketNumber(Long ticketNumber);

    /**
     * Getting the latest 10 tickets by default without
     *  https://stackoverflow.com/questions/24068884/spring-data-jpa-java-get-last-10-records-from-query/45334867#45334867
     * @param userId
     * @param storeId
     * @return the list of parking tickets
     */
    Optional<List<ParkinglotTicket>> findFirst10ByUserIdAndStoreIdOrderByCreatedAtDesc(Long userId, Long storeId);

    /**
     * https://thorben-janssen.com/hibernate-slow-query-log/
     * https://github.com/eugenp/tutorials/blob/master/persistence-modules/spring-data-jpa-query/src/main/java/com/baeldung/boot/passenger/PassengerRepositoryImpl.java
     * https://tech.asimio.net/2020/11/21/Implementing-dynamic-SQL-queries-using-Spring-Data-JPA-Specification-and-Criteria-API.html
     * Based on https://stackoverflow.com/questions/37167422/how-to-fetch-only-selected-attributes-of-an-entity-using-spring-jpa/37169276#37169276
     * @param userId
     * @param storeId
     * @return All the times that there's a ticket for a given user in a parking lot store
     */
    @Query("SELECT t.createdAt FROM ParkinglotTicket t where t.userId = :userId and t.storeId = :storeId ORDER BY t.createdAt asc")
    Optional<List<Long>> findAllDatesWithTicketsForUserInStore(@Param("userId") Long userId, @Param("storeId") Long storeId);

    /**
     * Pagination support https://www.programmersought.com/article/1459585922/
     * @return All the times that there's a ticket for a given user in a parking lot store
     */
    Optional<Page<ParkinglotTicket>> findAllByUserIdAndStoreId(Long userId, Long storeId, Pageable page);

    /**
     * @param userId is the user Id
     * @param storeId is the store Id
     * @param createdAt is the base of the date
     * @param createdAtOffset is the max date
     * @return The list of parking lot between two dates.
     */
    Optional<List<ParkinglotTicket>> findByUserIdAndStoreIdAndCreatedAtBetween(Long userId, Long storeId, Long createdAt, Long createdAtOffset);
}
