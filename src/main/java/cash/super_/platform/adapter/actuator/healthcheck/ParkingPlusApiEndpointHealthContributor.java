package cash.super_.platform.adapter.actuator.healthcheck;

import cash.super_.platform.adapter.actuator.HealthProbeVerifier;
import cash.super_.platform.autoconfig.ParkingPlusServiceClientProperties;
import cash.super_.platform.util.URLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;

/**
 * Health contributor
 * https://reflectoring.io/spring-boot-health-check/
 */
@Component("ParkingPlusAPI")
public class ParkingPlusApiEndpointHealthContributor implements HealthIndicator, HealthContributor {

    private static final Logger LOG = LoggerFactory.getLogger(HealthProbeVerifier.class);

    private static final String SERVICE_NAME = ParkingPlusApiEndpointHealthContributor.class.getName()
            .replace("EndpointHealthContributor", "");

    @Autowired
    private ParkingPlusServiceClientProperties properties;

    /**
     * If the URL is connected
     */
    private boolean hasConnection;

    public boolean hasConnectivity() {
        return this.hasConnection;
    }

    public URL getUrl() {
        return this.properties.getBaseUrl();
    }

    @Override
    public Health health() {
        // The service URL
        URL url = properties.getBaseUrl();

        // Check if the URL can be reached
        // TODO: IT MUST CALL THE HEALTHCHECK OF THE SERVICE AND VERIFY IF IT'S UP. DEEP HEALTHCHECK
        try (Socket socket = new Socket(url.getHost(), url.getPort())) {
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
