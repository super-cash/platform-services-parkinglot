package cash.super_.platform.clients.payment;

import cash.super_.platform.autoconfig.ClientProperties;
import cash.super_.platform.autoconfig.PlatformConfigurationProperties;
import cash.super_.platform.autoconfig.ParkingPlusProperties;
import cash.super_.platform.clients.DefaultObjectMapper;
import cash.super_.platform.clients.payment.errors.PaymentErrorHandler;
import cash.super_.platform.error.supercash.feign.SupercashErrorDecoder;
import cash.super_.platform.service.payment.model.pagarme.TransactionRequest;
import cash.super_.platform.service.payment.model.TransactionResponseSummary;
import cash.super_.platform.service.payment.model.pagseguro.TransactionResponse;
import feign.*;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@FeignClient(name = "paymentApiClient", url = "${cash.super.platform.service.payment.baseUrl}",
             configuration = PaymentServiceApiClient.ConfigurationForPaymentServiceApiClient.class)
public interface PaymentServiceApiClient {

    @RequestLine("POST /pagarme/transactions/pay")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
    })
    public TransactionResponseSummary requestPaymentUsingPagarme(TransactionRequest transaction);

    @RequestLine("POST /transactions/pay")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
    })
    public TransactionResponse requestPayment(cash.super_.platform.service.payment.model.pagseguro.TransactionRequest transaction);

    @RequestLine("GET /transactions/supercash/metadataby/{metadataKey}/{metadataValue}")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
    })
    public Map<String, String> getTransactionMetadata(@Param("metadataKey") String metadataKey,
                                                      @Param("metadataValue") String metadataValue);

    /* Config client class */
    @Component(value = "configurationForPaymentServiceApiClient")
    class ConfigurationForPaymentServiceApiClient {

        @Autowired
        private PlatformConfigurationProperties platformConfigurationProperties;

        @Autowired
        private ParkingPlusProperties parkingPlusProperties;

        @Autowired
        private ClientProperties clientProperties;

        @Autowired
        private DefaultObjectMapper objectMapper;

        @Bean(name = "builderForPaymentServiceApiClient")
        public Feign.Builder builderForPaymentServiceApiClient() {
            return Feign.builder()
                    .contract(new Contract.Default())
                    .logLevel(clientProperties.getClientLogLevel())
                    .encoder(new FormEncoder(new JacksonEncoder(objectMapper)))
                    .decoder(new JacksonDecoder(objectMapper))
                    .errorDecoder(new SupercashErrorDecoder(new PaymentErrorHandler()))
                    .retryer(new Retryer.Default(TimeUnit.SECONDS.toMillis(parkingPlusProperties.getRetryInterval()),
                            TimeUnit.SECONDS.toMillis(parkingPlusProperties.getRetryMaxPeriod()),
                            parkingPlusProperties.getRetryMaxAttempt()));
        }
    }
}