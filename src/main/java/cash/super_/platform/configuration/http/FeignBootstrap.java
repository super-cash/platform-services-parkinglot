package cash.super_.platform.configuration.http;

import feign.Contract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring has created their own Feign Contract to allow you to use Spring's @RequestMapping annotations instead of
 * Feigns. You can disable this behavior by including a bean of type feign.Contract.Default in your application context.
 *
 * If you're using spring-boot (or anything using Java config), including this in an @Configuration class should
 * re-enable Feign's annotations:
 *
 * More info: https://stackoverflow.com/questions/29985205/using-requestline-with-feign
 *
 */

@Configuration
public class FeignBootstrap {
    @Bean
    public Contract useFeignAnnotations() {
        return new Contract.Default();
    }
}
