package cash.super_.platform.service.parkinglot.payment;

import cash.super_.platform.service.pagarme.model.TransactionRequest;
import cash.super_.platform.service.pagarme.model.TransactionResponseSummary;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.Map;

public interface PagarmeClientService {

    @RequestLine("POST /transactions/credit")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
    })
    public TransactionResponseSummary requestPayment(TransactionRequest transaction);

    @RequestLine("GET /transactions/supercash/metadataby/{metadataKey}/{metadataValue}")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
    })
    public Map<String, String> getTransactionMetadata(@Param("metadataKey") String metadataKey,
                                                      @Param("metadataValue") String metadataValue);

}