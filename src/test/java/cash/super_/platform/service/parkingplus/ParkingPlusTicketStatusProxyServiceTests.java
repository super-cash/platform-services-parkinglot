package cash.super_.platform.service.parkingplus;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import cash.super_.platform.service.parkingplus.autoconfig.ParkingPlusProperties;
import cash.super_.platform.service.parkingplus.ticket.ParkingPlusTicketPaymentsProxyService;


// https://www.infoworld.com/article/3543268/junit-5-tutorial-part-2-unit-testing-spring-mvc-with-junit-5.html
// https://github.com/powermock/powermock/issues/1078
// https://stackoverflow.com/questions/63834940/springboot-cant-resolve-runwith-cannot-find-symbol/63835256#63835256
// https://www.baeldung.com/mockito-junit-5-extension
// https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-test-auto-configuration.html <<< Test Slices
@SpringBootTest(properties = {"cash.super.platform.service.parkingplus.prop=false"})
@DisplayName("ParkingPlust Status tests")
public class ParkingPlusTicketStatusProxyServiceTests {

  @Autowired
  private ParkingPlusProperties properties;

  @InjectMocks
  private ParkingPlusTicketPaymentsProxyService service;

  @Test
  @DisplayName("Test service distance matrix address")
  @Disabled
  public void testGetDistanceMatrixForAddresses() throws Exception {
//    ParkingTicket addresses = new ParkingTicket();
//    addresses.setDestinationAddress(DESTINATION);
//    addresses.setOriginAddress(ORIGIN);
//
//    DistanceMatrixElement element = new DistanceMatrixElement();
//    element.distance = new Distance();
//    element.distance.inMeters = 300L;
//    element.duration = new Duration();
//    element.duration.inSeconds = 44L;
//
//    DistanceMatrixRow row = new DistanceMatrixRow();
//    row.elements = new DistanceMatrixElement[] {element};
//
//    DistanceMatrix distanceMatrixResult = new DistanceMatrix(
//        new String[] {ORIGIN}, new String[]{DESTINATION}, new DistanceMatrixRow[]{row});
//
//    DistanceMatrixApiRequest distanceMatrixRequest = PowerMockito.mock(DistanceMatrixApiRequest.class, Mockito.RETURNS_DEEP_STUBS);
//    when(distanceMatrixRequest.origins(ORIGIN)).thenReturn(distanceMatrixRequest);
//    when(distanceMatrixRequest.destinations(DESTINATION)).thenReturn(distanceMatrixRequest);
//    when(distanceMatrixRequest.mode(TravelMode.DRIVING)).thenReturn(distanceMatrixRequest);
//    when(distanceMatrixRequest.avoid(RouteRestriction.TOLLS)).thenReturn(distanceMatrixRequest);
//    when(distanceMatrixRequest.language(properties.getLanguage())).thenReturn(distanceMatrixRequest);
//    when(distanceMatrixRequest.await()).thenReturn(distanceMatrixResult);
//
//    mockStatic(GeocodingApi.class);
//
//    when(DistanceMatrixApi.newRequest(eq(geoApiContext))).thenReturn(distanceMatrixRequest);
//
//    ParkingTicketStatus calculatedResult = service.getDriveDistance(addresses);
//
//    assertSame(distanceMatrixResult, calculatedResult);
  }

}
