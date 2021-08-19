package cash.super_.platform.client.payment;

import cash.super_.platform.model.supercash.PaymentChargeCaptureRequest;
import cash.super_.platform.model.supercash.types.charge.PaymentChargeResponse;
import cash.super_.platform.model.supercash.types.charge.AnonymousPaymentChargeRequest;
import cash.super_.platform.model.supercash.types.order.PaymentOrderRequest;
import cash.super_.platform.model.supercash.types.order.PaymentOrderResponse;
import feign.*;
import org.springframework.cloud.openfeign.FeignClient;
import cash.super_.platform.adapter.feign.SupercashDefaultFeignSettings;

import java.util.Map;

@FeignClient(name = "paymentApiClient", url = "${cash.super.platform.client.payment.baseUrl}",
             configuration = SupercashDefaultFeignSettings.class)
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
}