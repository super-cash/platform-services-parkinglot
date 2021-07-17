package cash.super_.platform.clients.payment;

import cash.super_.platform.autoconfig.ClientProperties;
import cash.super_.platform.autoconfig.PlatformConfigurationProperties;
import cash.super_.platform.autoconfig.ParkingPlusProperties;
import cash.super_.platform.clients.DefaultObjectMapper;
import cash.super_.platform.clients.payment.errors.PaymentErrorHandler;
import cash.super_.platform.error.supercash.feign.SupercashErrorDecoder;
import cash.super_.platform.service.payment.model.supercash.PaymentChargeCaptureRequest;
import cash.super_.platform.service.payment.model.supercash.types.charge.PaymentChargeRequest;
import cash.super_.platform.service.payment.model.supercash.types.charge.PaymentChargeResponse;
import cash.super_.platform.service.payment.model.supercash.types.charge.PaymentShortChargeRequest;
import cash.super_.platform.service.payment.model.supercash.types.order.PaymentOrderRequest;
import cash.super_.platform.service.payment.model.supercash.types.order.PaymentOrderResponse;
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
    public PaymentChargeResponse authorizePayment(PaymentShortChargeRequest paymentOrderRequest);

    @RequestLine("POST /pay/orders")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
    })
    public PaymentOrderResponse authorizePayment(PaymentOrderRequest paymentOrderRequest);

    @RequestLine("POST /{paymentId}/charges/{chargeId}/capture")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
    })
    public PaymentChargeResponse capturePayment(@Param("paymentId") Long paymentId,
                                                @Param("chargeId") String chargeId,
                                                PaymentChargeCaptureRequest paymentChargeCaptureRequest);

    @RequestLine("GET /metadataby/{metadataKey}/{metadataValue}")
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

        @Autowired
        private PaymentErrorHandler paymentErrorHandler;

        @Bean(name = "builderForPaymentServiceApiClient")
        public Feign.Builder builderForPaymentServiceApiClient() {
            return Feign.builder()
                    .contract(new Contract.Default())
                    .logLevel(clientProperties.getClientLogLevel())
                    .encoder(new FormEncoder(new JacksonEncoder(objectMapper)))
                    .decoder(new JacksonDecoder(objectMapper))
                    .errorDecoder(new SupercashErrorDecoder(paymentErrorHandler))
                    .retryer(new Retryer.Default(TimeUnit.SECONDS.toMillis(parkingPlusProperties.getRetryInterval()),
                            TimeUnit.SECONDS.toMillis(parkingPlusProperties.getRetryMaxPeriod()),
                            parkingPlusProperties.getRetryMaxAttempt()));
        }
    }
}