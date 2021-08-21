package cash.super_.platform.adapter.actuator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.PostConstruct;

import cash.super_.platform.adapter.actuator.healthcheck.ParkingLotTicketsStatusAPICompositeHealthContributor;
import cash.super_.platform.autoconfig.HealthcheckAdaptorProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

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
  private HealthcheckAdaptorProperties healthcheckAdaptorProperties;

  @Autowired
  private ParkingLotTicketsStatusAPICompositeHealthContributor statusApiContributors;

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
            healthcheckAdaptorProperties.getReadinessInterval(),
            healthcheckAdaptorProperties.getReadinessTimeUnit());

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

        // Fetch the supercash sales first and keep it in cache
        // Since it throws a runtime exception, just try it and verify the state below
        if (!statusApiContributors.parkingPlusApiEndpointHealthContributor.hasConnectivity()) {
          LOG.error("Parking Plus Readiness Health Probe: Failed. URL is unreachable: {}",
                  statusApiContributors.parkingPlusApiEndpointHealthContributor.getUrl());
          AvailabilityChangeEvent.publish(appContext, ReadinessState.REFUSING_TRAFFIC);
          AvailabilityChangeEvent.publish(appContext, LivenessState.BROKEN);

        } else
          // if the payments API is running
        if (!statusApiContributors.supercashPaymentsAPIHealthContributor.hasConnectivity()) {
          LOG.error("Supercash database Readiness Health Probe: Failed. can't query tickets. DB: {}",
                  statusApiContributors.parkingLotsDatabaseHealthContributor.getUrl());
          AvailabilityChangeEvent.publish(appContext, ReadinessState.REFUSING_TRAFFIC);
          AvailabilityChangeEvent.publish(appContext, LivenessState.BROKEN);
        } else

        // Fetch the supercash sales first and keep it in cache
        // Since it throws a runtime exception, just try it and verify the state below
        if (!statusApiContributors.parkingLotsDatabaseHealthContributor.isQueryValidOnConnection()) {
          LOG.error("Supercash database Readiness Health Probe: Failed. can't query tickets. DB: {}",
                  statusApiContributors.parkingLotsDatabaseHealthContributor.getUrl());
          AvailabilityChangeEvent.publish(appContext, ReadinessState.REFUSING_TRAFFIC);
          AvailabilityChangeEvent.publish(appContext, LivenessState.BROKEN);

        } else {
          LOG.info("Parking Plus Readiness Health Probes worked");
          AvailabilityChangeEvent.publish(appContext, LivenessState.CORRECT);
          AvailabilityChangeEvent.publish(appContext, ReadinessState.ACCEPTING_TRAFFIC);
        }

        LOG.debug("Current readiness={} liveness={}; Will check probe again in {} {}", 
            applicationAvailability.getReadinessState(), applicationAvailability.getLivenessState(),
                healthcheckAdaptorProperties.getReadinessInterval(),
                healthcheckAdaptorProperties.getReadinessTimeUnit());
      }

    }, 0L, healthcheckAdaptorProperties.getReadinessInterval(),
            healthcheckAdaptorProperties.getReadinessTimeUnit());
  }

}
