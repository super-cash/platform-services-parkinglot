package cash.super_.platform.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

@Validated
@Component
@ConfigurationProperties("cash.super.platform.adaptor.healthcheck")
public class HealthcheckAdaptorProperties {

    @Min(1)
    private Long readinessInterval;

    @NotNull
    private TimeUnit readinessTimeUnit;

    public Long getReadinessInterval() {
        return readinessInterval;
    }

    public void setReadinessInterval(Long readinessInterval) {
        this.readinessInterval = readinessInterval;
    }

    public TimeUnit getReadinessTimeUnit() {
        return readinessTimeUnit;
    }

    public void setReadinessTimeUnit(TimeUnit readinessTimeUnit) {
        this.readinessTimeUnit = readinessTimeUnit;
    }

    @Override
    public String toString() {
        return "HealthcheckAdaptorProperties{" +
                "readinessInterval=" + readinessInterval +
                ", readinessTimeUnit=" + readinessTimeUnit +
                '}';
    }
}
