package cash.super_.platform.configuration.actuator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.PostConstruct;

import cash.super_.platform.autoconfig.PlatformConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import cash.super_.platform.autoconfig.ParkinglotServiceProperties;
import cash.super_.platform.service.parkinglot.sales.ParkingPlusParkingSalesCachedProxyService;

/**
 * This is explained on
 * https://www.baeldung.com/spring-liveness-readiness-probes#1-readiness-and-liveness-state-transitions
 * Kubernetes will check the endpoint /actuator/health/liveness and it needs to return HTTP 200. If
 * this can't connect to the Parking Plus server for some reason, then the probe must fail.
 * 
 * https://spring.io/blog/2020/03/25/liveness-and-readiness-probes-with-spring-boot
 *
 * @author marcellodesales
 *
 */
@Component
public class HealthProbeVerifier {

  private static final Logger LOG = LoggerFactory.getLogger(HealthProbeVerifier.class);

  @Autowired
  private ApplicationAvailability applicationAvailability;

  @Autowired
  private ParkingPlusParkingSalesCachedProxyService salesCacheService;

  @Autowired
  private PlatformConfigurationProperties configurationProperties;

  @Autowired
  private ParkinglotServiceProperties properties;

  @Autowired
  private ApplicationContext appContext;

  @PostConstruct
  public void checkProbes() {
    // We need first to build the cache in order to be healthy / receive traffic
    // https://www.baeldung.com/spring-liveness-readiness-probes#actuator-probes
    AvailabilityChangeEvent.publish(appContext, ReadinessState.REFUSING_TRAFFIC);
    AvailabilityChangeEvent.publish(appContext, LivenessState.BROKEN);

    // Just refuse traffic if the sales cache is empty. This happens only once during the bootstrap
    // and when the cache needs to be rebuilt
    LOG.info("We can't receive traffic until the cache is built");

    LOG.debug("Current readiness={} liveness={}; Will check probe again in {} {}", 
        applicationAvailability.getReadinessState(), applicationAvailability.getLivenessState(),
            configurationProperties.getHealthProbe().getReadinessInterval(),
            configurationProperties.getHealthProbe().getReadinessTimeUnit());

    LOG.debug("Bootstrapping the probes helper");
    checkReadiness();
  }

  private void checkReadiness() {
    ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();
    scheduled.scheduleAtFixedRate(new Runnable() {

      @Override
      public void run() {
        // https://www.baeldung.com/spring-liveness-readiness-probes#1-readiness-and-liveness-state-transitions
        // enum called ReadinessState with the following values:
        // The ACCEPTING_TRAFFIC state represents that the application is ready to accept traffic
        // The REFUSING_TRAFFIC state means that the application is not willing to accept any requests yet

        try {
          // Fetch the supercash sales first and keep it in cache
          // Since it throws a runtime exception, just try it and verify the state below
          salesCacheService.fetchCurrentGarageSales();

        } catch (RuntimeException runtimeError) {
          LOG.error("Error populating the Sales Cache: {}", runtimeError.getMessage());
        }

        // The number of supercash sales must be available in order for the service to work as we depend on them
        if (salesCacheService.getCacheSize() == 0 || salesCacheService.getNumberOfSales() == 0) {
          LOG.error("Parking Plus Readiness Health Probe: Failed. Cache is Empty.");
          AvailabilityChangeEvent.publish(appContext, ReadinessState.REFUSING_TRAFFIC);
          AvailabilityChangeEvent.publish(appContext, LivenessState.BROKEN);

        } else {
          LOG.info("Parking Plus Readiness Health Probe: Built cache with {} entries",
              salesCacheService.getNumberOfSales());
          AvailabilityChangeEvent.publish(appContext, LivenessState.CORRECT);
          AvailabilityChangeEvent.publish(appContext, ReadinessState.ACCEPTING_TRAFFIC);
        }

        LOG.debug("Current readiness={} liveness={}; Will check probe again in {} {}", 
            applicationAvailability.getReadinessState(), applicationAvailability.getLivenessState(),
                configurationProperties.getHealthProbe().getReadinessInterval(),
                configurationProperties.getHealthProbe().getReadinessTimeUnit());
      }

    }, 0L, configurationProperties.getHealthProbe().getReadinessInterval(),
            configurationProperties.getHealthProbe().getReadinessTimeUnit());
  }

}
