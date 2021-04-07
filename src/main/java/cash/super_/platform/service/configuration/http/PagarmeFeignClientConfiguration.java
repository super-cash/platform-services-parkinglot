package cash.super_.platform.service.configuration.http;

import cash.super_.platform.error.supercash.feign.SupercashErrorDecoder;
import cash.super_.platform.service.parkinglot.autoconfig.ParkingPlusProperties;
import cash.super_.platform.service.parkinglot.payment.PagarmeClientService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.*;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import java.time.ZoneId;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Bootstraps the feign clients as described in the client documentation. Haven't found any documentation
 * on how to setup the client
 *
 * https://gitlab.com/supercash/clients/parking-plus-client-feign/-/blob/master/src/main/java/cash/super_/platform/client/parkingplus/invoker/ApiClient.java#L136-147
 * 
 * https://www.javacodegeeks.com/2018/06/provide-client-libraries-apis.html
 * https://arnoldgalovics.com/generating-feign-clients-with-swagger-codegen-and-gradle/
 * 
 * @author marcellodesales
 * @author leandromsales
 *
 */
@Configuration
public class PagarmeFeignClientConfiguration {

  @Autowired
  private ParkingPlusProperties properties;

  @Value("${cash.super.platform.service.pagarme.url}")
  @NotNull
  private String pagarmeServiceUrl;

  @Bean()
  public PagarmeClientService pagarmeServiceApi() {
    ObjectMapper objectMapper = createObjectMapper();
    Feign.Builder builder = Feign.builder()
            .logLevel(properties.getClientLogLevel())
            .encoder(new FormEncoder(new JacksonEncoder(objectMapper)))
            .decoder(new JacksonDecoder(objectMapper))
            .errorDecoder(new SupercashErrorDecoder(properties.getRetryableDestinationHosts()))
            .retryer(new Retryer.Default(TimeUnit.SECONDS.toMillis(properties.getRetryInterval()),
                    TimeUnit.SECONDS.toMillis(properties.getRetryMaxPeriod()), properties.getRetryMaxAttempt()));
    return builder.target(PagarmeClientService.class, pagarmeServiceUrl);
  }

  private ObjectMapper createObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setTimeZone(TimeZone.getTimeZone(properties.getTimeZone()));
    objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, false);
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
    objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, true);
    objectMapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, true);
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
    objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, false);
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerModule(new JodaModule());
    return objectMapper;
  }

}