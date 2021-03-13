package cash.super_.platform.service.configuration.actuator;

import cash.super_.platform.service.parkingplus.autoconfig.ParkingPlusProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Adds extra information about the service and how it is loaded to the actuator
 *
 * https://www.baeldung.com/spring-boot-info-actuator-custom
 */
@Component
public class ExtraInfoBootstrap implements InfoContributor {

    @Autowired
    private ParkingPlusProperties parkingPlusProperties;

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("host", parkingPlusProperties.getHost());
        userDetails.put("apiKey", String.valueOf(parkingPlusProperties.getApiKeyId()));
        builder.withDetail("parkingPlus", userDetails);
    }
}
