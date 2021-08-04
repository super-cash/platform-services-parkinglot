package cash.super_.platform.client;

import cash.super_.platform.autoconfig.ClientProperties;
import cash.super_.platform.service.configuration.json.datatime.LocalDateTimeConfig;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

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

@Component
public class DefaultObjectMapper extends ObjectMapper {

  @Autowired
  private ClientProperties clientProperties;

  @PostConstruct
  public void setupObjectMapper() {
    // In order to serialize the date time properly
    if (clientProperties != null) {
      this.setTimeZone(TimeZone.getTimeZone(clientProperties.getTimeZone()));
    }
    this.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
    this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // this.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
    this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    this.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, true);
    this.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, true);
    this.addMixIn(Exception.class, ExceptionMixIn.class);
    this.addMixIn(Throwable.class, ThrowableMixIn.class);
    // this.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);

    JavaTimeModule jtm = new JavaTimeModule();
    jtm.addSerializer(LocalTime.class, new
            LocalTimeSerializer(DateTimeFormatter.ofPattern(LocalDateTimeConfig.timeFormat)));
    jtm.addSerializer(LocalDate.class, new
            LocalDateSerializer(DateTimeFormatter.ofPattern(LocalDateTimeConfig.dateFormat)));
    jtm.addSerializer(LocalDateTime.class, new
            LocalDateTimeSerializer(DateTimeFormatter.ofPattern(LocalDateTimeConfig.dateTimeFormat)));
    jtm.addDeserializer(LocalTime.class, new
            LocalTimeDeserializer(DateTimeFormatter.ofPattern(LocalDateTimeConfig.timeFormat)));
    jtm.addDeserializer(LocalDate.class, new
            LocalDateDeserializer(DateTimeFormatter.ofPattern(LocalDateTimeConfig.dateFormat)));
    jtm.addDeserializer(LocalDateTime.class, new
            LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(LocalDateTimeConfig.dateTimeFormat)));

    this.registerModule(jtm);
  }

  @JsonIgnoreProperties({"stackTrace", "cause", "message", "suppressed", "localizedMessage"})
  public abstract class ExceptionMixIn extends Exception {

    @JsonCreator
    public ExceptionMixIn(@JsonProperty("message") String message) {
      super(message);
    }
  }

  @JsonIgnoreProperties({"stackTrace", "cause", "message", "suppressed", "localizedMessage"})
  public abstract class ThrowableMixIn extends Throwable {

    @JsonCreator
    public ThrowableMixIn(@JsonProperty("message") String message) {
      super(message);
    }
  }
}