package cash.super_.platform.service.configuration.http;

import cash.super_.platform.service.parkinglot.RequestInterceptorAdapter;
import cash.super_.platform.service.parkinglot.repository.MarketplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RequestInterceptorConfiguration implements WebMvcConfigurer {

    @Autowired
    private MarketplaceRepository marketplaceRepository;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        RequestInterceptorAdapter ria = new RequestInterceptorAdapter();
        ria.setMarketplaceRepository(marketplaceRepository);
        registry.addInterceptor(ria);
    }

}