package cash.super_.platform.client;

import cash.super_.platform.autoconfig.PlatformConfigurationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class ObjectMapperConfiguration implements WebMvcConfigurer {

    @Autowired
    private PlatformConfigurationProperties platformConfigurationProperties;

    @Autowired
    private DefaultObjectMapper defaultObjectMapper;

    @Bean(name = "jsonMapper")
    @Primary
    public ObjectMapper jsonMapper() {
        return defaultObjectMapper;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(jsonMapper()));
    }
}