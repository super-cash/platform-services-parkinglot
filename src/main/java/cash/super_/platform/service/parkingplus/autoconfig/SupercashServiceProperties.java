package cash.super_.platform.service.parkingplus.autoconfig;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Supercash service properties to be used by all services in Kubernetes. Must have a superclass with the actual props
 * 
 * https://stackoverflow.com/questions/53962547/polymorphic-configuration-properties-in-spring-boot
 *
 * @author marcellodesales
 *
 */
public class SupercashServiceProperties {

  @NotBlank
  private String apiVersion;

  @NotNull
  private KubernetesProbeProperties healthProbe;

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public KubernetesProbeProperties getHealthProbe() {
    return healthProbe;
  }

  public void setHealthProbe(KubernetesProbeProperties healthProbe) {
    this.healthProbe = healthProbe;
  }

}
