package cash.super_.platform.service.parkinglot.repository;

import cash.super_.platform.service.payment.model.supercash.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Database {

    private static final Logger LOG = LoggerFactory.getLogger(Database.class);

    @Autowired
    private PaymentRepository paymentRepository;

    public void savePayment(Payment payment) {
        try {
//            LOG.debug("Object to save in database: {}", paymentOrderResponse);
            /*
             * TODO: Fault tolerance
             * In case we fail to save the TransactionResponse in our database, we have to handle this situation
             * because the transaction was completed there in Pagarme, so that we can't simply report an error to the
             * requester. The best is to use async handler based on queue approach.
             */
            paymentRepository.save(payment);

        } catch (Exception persistenceError) {
            String errorMessage = String.format("Error while persisting object in database: '%s'",
                    persistenceError.getMessage());
            LOG.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
}
