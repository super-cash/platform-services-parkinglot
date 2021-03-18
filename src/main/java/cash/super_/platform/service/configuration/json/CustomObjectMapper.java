package cash.super_.platform.service.configuration.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CustomObjectMapper extends ObjectMapper {

    public CustomObjectMapper() {
        super();
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, true);
        configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, true);
        addMixIn(Exception.class, ExceptionMixIn.class);
        addMixIn(Throwable.class, ThrowableMixIn.class);
        registerModule(new JavaTimeModule());
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