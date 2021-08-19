package cash.super_.platform.adapter.feign;

import cash.super_.platform.autoconfig.ClientProperties;
import cash.super_.platform.client.DefaultObjectMapper;
import cash.super_.platform.client.payment.error.PaymentErrorHandler;
import feign.Contract;
import feign.Feign;
import feign.Retryer;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * The default settings for all the feign clients, can be reused.
 */
@Component
public class SupercashDefaultFeignSettings {

    @Autowired
    private ClientProperties feignClientProperties;

    @Autowired
    private DefaultObjectMapper objectMapper;

    @Autowired
    private PaymentErrorHandler paymentErrorHandler;

    @Bean
    public Feign.Builder builderForPaymentServiceApiClient() {
        return Feign.builder()
                .contract(new Contract.Default())
                .logLevel(feignClientProperties.getLogLevel())
                .encoder(new FormEncoder(new JacksonEncoder(objectMapper)))
                .decoder(new JacksonDecoder(objectMapper))
                .errorDecoder(new SupercashErrorDecoder(paymentErrorHandler))
                .retryer(new Retryer.Default(TimeUnit.SECONDS.toMillis(feignClientProperties.getRetryInterval()),
                        TimeUnit.SECONDS.toMillis(feignClientProperties.getRetryMaxPeriod()),
                        feignClientProperties.getRetryMaxAttempt()));
    }
}