package cash.super_.platform.adapter.actuator.healthcheck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.NamedContributor;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * It should display the full results of the healthcheck
 *
 * "getParkingTicketStatusAPI": {
 *    "status": "UP",
 *    "components": {
 *       "supercashPostgresDatabase": {
 *          "status": "UP"
 *       },
 *       "parkingPlusApiEndpoint": {
 *          "status": "UP"
 *       }
 *    }
 * },
 */
@Component("getParkingTicketStatusAPI")
public class ParkingLotTicketsStatusAPICompositeHealthContributor implements CompositeHealthContributor {

    private Map<String, HealthContributor> contributors = new LinkedHashMap<>();

    public ParkingLotsDatabaseHealthContributor parkingLotsDatabaseHealthContributor;
    public ParkingPlusApiEndpointHealthContributor parkingPlusApiEndpointHealthContributor;
    public SupercashPaymentsAPIHealthContributor supercashPaymentsAPIHealthContributor;

    @Autowired
    public ParkingLotTicketsStatusAPICompositeHealthContributor(ParkingLotsDatabaseHealthContributor databaseHealthContributor,
                                                                ParkingPlusApiEndpointHealthContributor parkingPlusApiEndpointHealthContributor,
                                                                SupercashPaymentsAPIHealthContributor supercashPaymentsAPIHealthContributor) {
        contributors.put("supercashPostgresDatabase", databaseHealthContributor);
        contributors.put("supercashPaymentsService", supercashPaymentsAPIHealthContributor);
        contributors.put("parkingPlusApiEndpoint", parkingPlusApiEndpointHealthContributor);
    }

    /**
     *  return list of health contributors
     */
    @Override
    public Iterator<NamedContributor<HealthContributor>> iterator() {
        return contributors.entrySet().stream()
                .map((entry) ->
                        NamedContributor.of(entry.getKey(),
                                entry.getValue())).iterator();
    }

    @Override
    public HealthContributor getContributor(String name) {
        return contributors.get(name);
    }

}