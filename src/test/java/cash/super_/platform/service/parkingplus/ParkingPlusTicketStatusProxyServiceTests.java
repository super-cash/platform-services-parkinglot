package cash.super_.platform.service.distancematrix;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.google.maps.DirectionsApi.RouteRestriction;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.Distance;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.Duration;
import com.google.maps.model.TravelMode;
import cash.super_.platform.service.distancematrix.model.DistanceMatrixAddresses;
import cash.super_.platform.service.distancematrix.model.DistanceMatrixResult;


// https://www.infoworld.com/article/3543268/junit-5-tutorial-part-2-unit-testing-spring-mvc-with-junit-5.html
// https://github.com/powermock/powermock/issues/1078
// https://stackoverflow.com/questions/63834940/springboot-cant-resolve-runwith-cannot-find-symbol/63835256#63835256
// https://www.baeldung.com/mockito-junit-5-extension
@SpringBootTest(properties = {"cash.super.platform.service.distancematrix.googleMapsApiToken=fake-token"})
@DisplayName("Distance Matrix Service Tests Mocking Google GeoAPI")
public class DistanceMatrixGoogleGeoAPICachedProxyServiceTests {

  private static final String ORIGIN = "Maceio, Alagoas, Brazil";
  private static final String DESTINATION = "Recife, Pernambuco, Brazil";

  @Autowired
  private DistanceMatrixProperties properties;

  @Mock
  private GeoApiContext geoApiContext;

  @InjectMocks
  private DistanceMatrixGoogleGeoAPICachedProxyService service;

  @Test
  @DisplayName("Test service to Distance Matrix Success mocking Google Geo API")
  @Disabled
  public void testGetDistanceMatrixForAddresses() throws Exception {
    DistanceMatrixAddresses addresses = new DistanceMatrixAddresses();
    addresses.setDestinationAddress(DESTINATION);
    addresses.setOriginAddress(ORIGIN);

    DistanceMatrixElement element = new DistanceMatrixElement();
    element.distance = new Distance();
    element.distance.inMeters = 300L;
    element.duration = new Duration();
    element.duration.inSeconds = 44L;

    DistanceMatrixRow row = new DistanceMatrixRow();
    row.elements = new DistanceMatrixElement[] {element};

    DistanceMatrix distanceMatrixResult = new DistanceMatrix(
        new String[] {ORIGIN}, new String[]{DESTINATION}, new DistanceMatrixRow[]{row});

    DistanceMatrixApiRequest distanceMatrixRequest = PowerMockito.mock(DistanceMatrixApiRequest.class, Mockito.RETURNS_DEEP_STUBS);
    when(distanceMatrixRequest.origins(ORIGIN)).thenReturn(distanceMatrixRequest);
    when(distanceMatrixRequest.destinations(DESTINATION)).thenReturn(distanceMatrixRequest);
    when(distanceMatrixRequest.mode(TravelMode.DRIVING)).thenReturn(distanceMatrixRequest);
    when(distanceMatrixRequest.avoid(RouteRestriction.TOLLS)).thenReturn(distanceMatrixRequest);
    when(distanceMatrixRequest.language(properties.getLanguage())).thenReturn(distanceMatrixRequest);
    when(distanceMatrixRequest.await()).thenReturn(distanceMatrixResult);

    mockStatic(GeocodingApi.class);

    when(DistanceMatrixApi.newRequest(eq(geoApiContext))).thenReturn(distanceMatrixRequest);

    DistanceMatrixResult calculatedResult = service.getDriveDistance(addresses);

    assertSame(distanceMatrixResult, calculatedResult);
  }

}
