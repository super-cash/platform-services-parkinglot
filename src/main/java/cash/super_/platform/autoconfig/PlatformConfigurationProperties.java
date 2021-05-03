package cash.super_.platform.autoconfig;

import cash.super_.platform.utils.URLUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URL;

@Validated
@Component
@ConfigurationProperties(prefix = "cash.super.platform.configuration")
public class PlatformConfigurationProperties extends PlatformProperties {

    @NotBlank
    private String apiUrl;

    @NotBlank
    private String orchestratorUrl;

    @Min(1)
    @Max(65535)
    private Integer defaultHeathCheckPort;

    @NotNull
    private KubernetesProbeProperties healthProbe;

    @NotNull
    private String timeZone;

    @PostConstruct
        private void validateURLs() {
        URL url = URLUtils.validateURL(apiUrl, defaultHeathCheckPort);
        apiUrl = url.toString();

        url = URLUtils.validateURL(orchestratorUrl, defaultHeathCheckPort);
        orchestratorUrl = url.toString();
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getOrchestratorUrl() {
        return orchestratorUrl;
    }

    public void setOrchestratorUrl(String orchestratorUrl) {
        this.orchestratorUrl = orchestratorUrl;
    }

    public Integer getDefaultHeathCheckPort() {
        return defaultHeathCheckPort;
    }

    public void setDefaultHeathCheckPort(Integer defaultHeathCheckPort) {
        this.defaultHeathCheckPort = defaultHeathCheckPort;
    }

    public KubernetesProbeProperties getHealthProbe() {
        return healthProbe;
    }

    public void setHealthProbe(KubernetesProbeProperties healthProbe) {
        this.healthProbe = healthProbe;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public String toString() {
        return "ConfigurationProperties{" +
                "apiUrl='" + apiUrl + '\'' +
                ", orchestratorUrl='" + orchestratorUrl + '\'' +
                ", orchestratorPort=" + defaultHeathCheckPort +
                ", healthProbe=" + healthProbe +
                ", timeZone='" + timeZone + '\'' +
                '}';
    }
}
