package cash.super_.platform.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * TODO: THIS MUST GO TO THE CLIENT API AS WELL
 */
@Validated
@Component
@ConfigurationProperties(prefix = "cash.super.platform.client.payment")
public class PaymentsServiceProperties {

    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
