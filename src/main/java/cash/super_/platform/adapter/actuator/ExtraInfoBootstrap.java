package cash.super_.platform.adapter.actuator;

import cash.super_.platform.autoconfig.ParkinglotServiceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adds extra information about the service and how it is loaded to the actuator
 *
 * https://www.baeldung.com/spring-boot-info-actuator-custom
 */
@Component
public class ExtraInfoBootstrap implements InfoContributor {

    @Autowired
    private ParkinglotServiceProperties parkingPlusProperties;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private Environment environment;

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, String> parkingPlusProps = new HashMap<>();
        parkingPlusProps.put("host", parkingPlusProperties.getBaseUrl());
        parkingPlusProps.put("apiKey", String.valueOf(parkingPlusProperties.getApiKeyId()));
        builder.withDetail("parkingPlus", parkingPlusProps);


        Map<String, String> envProps = new HashMap<>();
        envProps.put("profiles", Arrays.stream(environment.getActiveProfiles()).collect(Collectors.joining(",")));
        envProps.put("postgresUrl", dataSourceProperties.getDataUsername() + ":***@" + dataSourceProperties.getUrl() );
        envProps.put("apiKey", String.valueOf(parkingPlusProperties.getApiKeyId()));
        builder.withDetail("supercash", envProps);
    }


}
