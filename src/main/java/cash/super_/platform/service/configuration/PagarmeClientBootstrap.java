package cash.super_.platform.service.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.pagar.model.Address;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.joda.time.DateTime;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

@Component
public class PagarmeClientBootstrap {

    // this will exclude nullField
    @JsonInclude(JsonInclude.Include.NON_NULL)
    interface PagarmeAddreessMixin {
        @JsonProperty("createdAt")
        DateTime getCreatedAt();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer addressCustomizer() {
        System.out.println("Passou!");
        return jacksonObjectMapperBuilder ->
                jacksonObjectMapperBuilder.mixIn(Address.class, PagarmeAddreessMixin.class);
    }
}
