package cash.super_.platform.service.parkingplus.autoconfig;

import java.util.concurrent.TimeUnit;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

}
