package cash.super_.platform.autoconfig;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

public class KubernetesProbeProperties {

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
        return "KubernetesProbeProperties{" +
                "readinessInterval=" + readinessInterval +
                ", readinessTimeUnit=" + readinessTimeUnit +
                '}';
    }
}
