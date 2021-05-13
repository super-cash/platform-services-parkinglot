package cash.super_.platform.service.parkinglot.repository;

import cash.super_.platform.service.payment.model.supercash.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

//    @Query("select p from PaymentCharge p join p.metadata m where (KEY(m) = :name and m = :value)")
//    Optional<PaymentCharge> findPaymentByMetadata(@Param("name") String name, @Param("value") String value);

//    Optional<Payment> findByUuid(UUID uuid);

}
