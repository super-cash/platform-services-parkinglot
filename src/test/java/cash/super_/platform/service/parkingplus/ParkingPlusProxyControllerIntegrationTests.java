package cash.super_.platform.service.parkingplus;

import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Requires network connectivity as it will call Google's API.
 *
 * @author marcellodesales
 *
 */
@SpringBootTest(properties = {},
    webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("Distance Matrix Controller Integration Tests")
@ActiveProfiles({"integration_tests"})
public class ParkingPlusProxyControllerIntegrationTests {

  @LocalServerPort
  int randomServerPort;

  private static final String ORIGIN = "Maceio, Alagoas, Brazil";
  private static final String DESTINATION = "Recife, Pernambuco, Brazil";

  @Autowired
  private ParkingPlusProperties properties;

  private URI controllerEndpoint;

  @BeforeEach
  public void setup() throws URISyntaxException {
    String hostname = "http://localhost";
    String apiVersion = properties.getApiVersion();
    String baseUrl =
        String.format("%s:%d/%s%s", hostname, randomServerPort, apiVersion, ParkingPlusProxyController.BASE_ENDPOINT);
    controllerEndpoint = new URI(baseUrl);
  }

  @Test
  @DisplayName("Test Get Distance Matrix Success")
  public void testGetDistanceMatrixForAddresses() throws Exception {
//    ParkingTicket addresses = new ParkingTicket();
//    addresses.setDestinationAddress(DESTINATION);
//    addresses.setOriginAddress(ORIGIN);
//
//    RestTemplate restTemplate = new RestTemplate();
//
//
//    HttpHeaders headers = new HttpHeaders();
//    headers.set("supercash_tid", "transaction-" + UUID.randomUUID().toString().substring(0, 7));
//    headers.set("supercash_cid", "customer-" + UUID.randomUUID().toString().substring(0, 7));
//
//    HttpEntity<ParkingTicket> request = new HttpEntity<>(addresses, headers);
//
//    ResponseEntity<ParkingTicketStatus> result =
//        restTemplate.postForEntity(controllerEndpoint, request, ParkingTicketStatus.class);
//
//    // Verify protocol requirements
//    assertThat(result.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
//    assertThat(result.getHeaders()).containsKey("X-Trace-ID");
//    assertThat(result.getHeaders().get("X-Trace-ID")).isNotEmpty();
//
//    // Verify the payload
//    assertThat(result.getBody().getDistance()).isEqualTo(257055);
//    assertThat(result.getBody().getTime()).isGreaterThan(13510).isLessThanOrEqualTo(13520);
  }

}
