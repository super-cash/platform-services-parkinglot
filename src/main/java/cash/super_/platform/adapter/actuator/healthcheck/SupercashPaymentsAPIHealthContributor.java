package cash.super_.platform.adapter.actuator.healthcheck;

import cash.super_.platform.autoconfig.PaymentsServiceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

/**
 * Health contributor
 * https://reflectoring.io/spring-boot-health-check/
 */
@Component("SupercashPaymentsAPI")
public class SupercashPaymentsAPIHealthContributor implements HealthIndicator, HealthContributor {

    private static final Logger LOG = LoggerFactory.getLogger(SupercashPaymentsAPIHealthContributor.class);

    private static final String SERVICE_NAME = SupercashPaymentsAPIHealthContributor.class.getName()
            .replace("Supercash", "").replace("API", "");

    @Autowired
    private PaymentsServiceProperties properties;

    /**
     * If the URL is connected
     */
    private boolean hasConnection;

    public boolean hasConnectivity() {
        return this.hasConnection;
    }

    public String getUrl() {
        return this.properties.getBaseUrl();
    }

    @Override
    public Health health() {
        // Check if the URL is correct
        URL url = null;
        try {
            url = new URL(properties.getBaseUrl());

        } catch (MalformedURLException urlError) {
            LOG.error("The WPS URL seems to be malformed: {}", properties.getBaseUrl());
            return Health.down().withDetail("error", urlError.getMessage()).build();
        }

        int port = url.getPort() == -1
                ? (url.getProtocol().startsWith("https") ? 443 : 80)
                :  url.getPort();

        // Check if the URL can be reached
        // TODO: IT MUST CALL THE HEALTHCHECK OF THE SERVICE AND VERIFY IF IT'S UP. DEEP HEALTHCHECK
        try (Socket socket = new Socket(url.getHost(), port)) {
            this.hasConnection = true;
            LOG.debug("Connection with '{}' successfully made at {}", SERVICE_NAME, url);

        } catch (IOException errorConnecting) {
            this.hasConnection = false;
            LOG.error("Failed healthcheck probe: Can't connect to service {} at {}: {}", SERVICE_NAME, url, errorConnecting.getMessage());
            return Health.down().withDetail("error", errorConnecting.getMessage()).build();
        }
        return Health.up().build();
    }
}
