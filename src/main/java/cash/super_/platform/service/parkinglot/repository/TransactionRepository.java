package cash.super_.platform.service.parkinglot.repository;

import cash.super_.platform.service.payment.model.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

//    Transaction findById(Long id);

    @Query("SELECT t from Transaction t JOIN t.metadata m WHERE ?1 in (VALUE(m))")
    Optional<Transaction> findByMetadata(String value);

    @Query("select t from Transaction t join t.metadata m where (KEY(m) = :name and m = :value)")
    Optional<Transaction> findTransactionByMetadata(@Param("name") String name, @Param("value") String value);


}
