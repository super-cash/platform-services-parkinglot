package cash.super_.platform.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
@Component
@ConfigurationProperties(prefix = "cash.super.platform.adaptor")
public class PlatformAdaptorProperties {

    @NotNull
    private HealthcheckAdaptorProperties healthcheck;

    public HealthcheckAdaptorProperties getHealthcheck() {
        return healthcheck;
    }

    public void setHealthcheck(HealthcheckAdaptorProperties healthcheck) {
        this.healthcheck = healthcheck;
    }

    @Override
    public String toString() {
        return "PlatformAdaptor{" +
                "healthcheck='" + healthcheck + '\'' +
                '}';
    }
}
