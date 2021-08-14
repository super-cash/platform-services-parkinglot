package cash.super_.platform.client.payment;

import cash.super_.platform.autoconfig.ClientProperties;
import cash.super_.platform.client.DefaultObjectMapper;
import cash.super_.platform.client.payment.error.PaymentErrorHandler;
import cash.super_.platform.adapter.feign.SupercashErrorDecoder;
import cash.super_.platform.model.supercash.PaymentChargeCaptureRequest;
import cash.super_.platform.model.supercash.types.charge.PaymentChargeResponse;
import cash.super_.platform.model.supercash.types.charge.AnonymousPaymentChargeRequest;
import cash.super_.platform.model.supercash.types.order.PaymentOrderRequest;
import cash.super_.platform.model.supercash.types.order.PaymentOrderResponse;
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

    @RequestLine("POST /pay/charges")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
    })
    PaymentChargeResponse authorizePayment(AnonymousPaymentChargeRequest paymentOrderRequest);

    @RequestLine("POST /pay/orders")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
    })
    PaymentOrderResponse authorizePayment(PaymentOrderRequest paymentOrderRequest);

    @RequestLine("POST /{paymentId}/charges/{chargeId}/capture")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
    })
    PaymentChargeResponse capturePayment(@Param("paymentId") Long paymentId,
                                                @Param("chargeId") String chargeId,
                                                PaymentChargeCaptureRequest paymentChargeCaptureRequest);

    @RequestLine("GET /metadataby/{metadataKey}/{metadataValue}")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
    })
    Map<String, String> getTransactionMetadata(@Param("metadataKey") String metadataKey,
                                                      @Param("metadataValue") String metadataValue);

    /* Config client class */
    @Component
    class ConfigurationForPaymentServiceApiClient {

        @Autowired
        private ClientProperties feignClientProperties;

        @Autowired
        private ClientProperties clientProperties;

        @Autowired
        private DefaultObjectMapper objectMapper;

        @Autowired
        private PaymentErrorHandler paymentErrorHandler;

        @Bean
        public Feign.Builder builderForPaymentServiceApiClient() {
            return Feign.builder()
                    .contract(new Contract.Default())
                    .logLevel(clientProperties.getLogLevel())
                    .encoder(new FormEncoder(new JacksonEncoder(objectMapper)))
                    .decoder(new JacksonDecoder(objectMapper))
                    .errorDecoder(new SupercashErrorDecoder(paymentErrorHandler))
                    .retryer(new Retryer.Default(TimeUnit.SECONDS.toMillis(feignClientProperties.getRetryInterval()),
                            TimeUnit.SECONDS.toMillis(feignClientProperties.getRetryMaxPeriod()),
                            feignClientProperties.getRetryMaxAttempt()));
        }
    }
}