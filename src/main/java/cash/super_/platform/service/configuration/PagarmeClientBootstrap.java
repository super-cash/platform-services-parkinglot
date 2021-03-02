package cash.super_.platform.service.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.pagar.model.Address;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

/**
 * Example of overriding the API we can't control as the getCreatedAt fails with the error. the Address from the Pagarme
 * object has a Runtime exception in GetAddress
 *
 * Caused by: org.springframework.http.converter.HttpMessageNotWritableException: Could not write JSON: Not allowed.;
 * nested exception is com.fasterxml.jackson.databind.JsonMappingException: Not allowed.
 * (through reference chain: me.pagar.model.Transaction["billing"]->me.pagar.model.Billing["address"]->me.pagar.model.Address["createdAt"])
 *
 * The only way to fix is to add a mixin to change the semantics of the get and NOT make it serializable.
 *
 * Mixin Example of changing an API
 * https://stackoverflow.com/questions/7421474/how-can-i-tell-jackson-to-ignore-a-property-for-which-i-dont-have-control-over/7422285#7422285
 *
 * Example changing the property we need to change
 * https://gist.github.com/m-x-k/e0208e1d6452e2b16d3b5c9651d5f760
 */
@Component
public class PagarmeClientBootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(SleuthResponseTraceIdInjectorFilter.class);

    // this will exclude nullField
    @JsonInclude(JsonInclude.Include.NON_NULL)
    interface PagarmeAddreessMixin {

        // Override the current value of getCreatedAt saying that it should be ignored, since we don't have control of that API
        @JsonIgnore
        abstract DateTime getCreatedAt();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer addressCustomizer() {
        LOG.debug("Adding the mixin for Pagame Address class");
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.mixIn(Address.class, PagarmeAddreessMixin.class);
    }
}
