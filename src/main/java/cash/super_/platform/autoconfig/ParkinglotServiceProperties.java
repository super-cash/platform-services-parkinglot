package cash.super_.platform.autoconfig;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * The properties for the service.
 * TODO: Move to a shared library.
 */
@Validated
@Component
@ConfigurationProperties(prefix = "cash.super.platform.service.parkinglot")
public class ParkinglotServiceProperties {

    protected static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ParkinglotServiceProperties.class);

    @Autowired
    private PlatformAdaptorProperties platformConfigurationProperties;

    @NotBlank
    private String apiVersion;

    @NotBlank
    private String healthcheckPostgresProbeQuery;

    @NotNull
    private List<String> retryableDestinationHosts = new ArrayList<String>();

    public PlatformAdaptorProperties getPlatformConfigurationProperties() {
        return platformConfigurationProperties;
    }

    public void setPlatformConfigurationProperties(PlatformAdaptorProperties platformConfigurationProperties) {
        this.platformConfigurationProperties = platformConfigurationProperties;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String pagseguroApiVersion) {
        this.apiVersion = pagseguroApiVersion;
    }

    public String getHealthcheckPostgresProbeQuery() {
        return healthcheckPostgresProbeQuery;
    }

    public void setHealthcheckPostgresProbeQuery(String healthcheckPostgresProbeQuery) {
        this.healthcheckPostgresProbeQuery = healthcheckPostgresProbeQuery;
    }

    @Override
    public String toString() {
        return "ParkinglotServiceProperties{" +
                "platformConfigurationProperties=" + platformConfigurationProperties +
                ", apiVersion='" + apiVersion + '\'' +
                ", healthcheckPostgresProbeQuery='" + healthcheckPostgresProbeQuery + '\'' +
                ", retryableDestinationHosts=" + retryableDestinationHosts +
                '}';
    }
}
