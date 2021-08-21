package cash.super_.platform.adapter.actuator.healthcheck;

import cash.super_.platform.adapter.actuator.HealthProbeVerifier;
import cash.super_.platform.autoconfig.ParkinglotServiceProperties;
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
@Component("ParkingPlusAPI")
public class ParkingPlusApiEndpointHealthContributor implements HealthIndicator, HealthContributor {

    private static final Logger LOG = LoggerFactory.getLogger(HealthProbeVerifier.class);

    @Autowired
    private ParkinglotServiceProperties properties;

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

        // Check if the URL can be reached
        try (Socket socket = new Socket(url.getHost(), url.getPort())) {
            this.hasConnection = true;
            LOG.debug("Connection with WPS working: {}", properties.getBaseUrl());

        } catch (IOException errorConnecting) {
            this.hasConnection = false;
            LOG.error("Failed healthcheck probe: Can't connect to {}: {}", url, errorConnecting.getMessage());
            return Health.down().withDetail("error", errorConnecting.getMessage()).build();
        }
        return Health.up().build();
    }
}
