package cash.super_.platform.service.parkingplus.payment;

import cash.super_.platform.service.pagarme.transactions.models.TransactionRequest;
import cash.super_.platform.service.pagarme.transactions.models.TransactionResponseSummary;
import feign.Headers;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

public interface PagarmeClientService {

    @RequestLine("POST /transactions/credit")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
    })
    public TransactionResponseSummary requestPayment(TransactionRequest transaction);

    @RequestLine("GET /transactions/testBadGateway")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
    })
    public void requestTestBadGateway();

}