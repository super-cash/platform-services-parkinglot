package cash.super_.platform.adapter.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This is the initial configuration for the web context. It creates an empty supercash context
 * for all requests that will be filled out by the Interceptor.
 * The scope of all calls for Supercash that's populated by the SupercashSecurityInterceptor.
 * Based on https://dzone.com/articles/using-requestscope-with-your-api, https://gitlab.com/johnjvester/request-scope.
 */
@Configuration
public class SupercashWebConfig implements WebMvcConfigurer {

    @Bean
    public SupercashSecurityInterceptor getSecurityInterceptor() {
        return new SupercashSecurityInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getSecurityInterceptor()).addPathPatterns("/**");
    }

    @Bean
    @RequestScope
    public SupercashRequestContext supercashRequestContextInstance() {
        return new SupercashRequestContext();
    }
}

