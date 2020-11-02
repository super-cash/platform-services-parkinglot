package cash.super_.platform.service.distancematrix;

import java.io.IOException;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.maps.DirectionsApi.RouteRestriction;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.TravelMode;
import cash.super_.platform.service.DistanceMatrixProperties;
import cash.super_.platform.service.distancematrix.model.DistanceMatrixAddresses;
import cash.super_.platform.service.distancematrix.model.DistanceMatrixResult;
import cash.super_.platform.service.distancematrix.util.StringUtil;

@Service
public class DistanceMatrixService {

  private static final Logger LOG = LoggerFactory.getLogger(DistanceMatrixService.class);

  @Autowired
  private DistanceMatrixProperties properties;

  /**
   * The API is very expensive to be built
   */
  private GeoApiContext geoApi;

  @PostConstruct
  public void postConstruct() {
    // this call if extremely expensive and must be cached
    String googleApiToken = properties.getGoogleMapsApiToken();
    LOG.info("Bootstrapping the Google Geo API with token: {}", StringUtil.obsfucate(googleApiToken) );
    geoApi = new GeoApiContext.Builder().apiKey(googleApiToken).build();
    LOG.info("Initialized Google Geo API");
  }

  public DistanceMatrixResult getDriveDistance(DistanceMatrixAddresses addresses)
      throws ApiException, InterruptedException, IOException {

    LOG.debug("Got the addresses: %s", addresses);

    // Verify the input of addresses
    Preconditions.checkArgument(addresses != null, "The addresses must be provided");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(addresses.getOriginAddress()), "The origin must be provided");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(addresses.getDestinationAddress()),
        "The destination must be provided");

    if (geoApi == null) {
      throw new IllegalArgumentException(
          String.format("Error getting the GeoAPI with the given token %s", properties.getGoogleMapsApiToken()));
    }

    DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(geoApi);
    DistanceMatrix result = req
        .origins(addresses.getOriginAddress())
        .destinations(addresses.getDestinationAddress())
        .mode(TravelMode.DRIVING)
        .avoid(RouteRestriction.TOLLS)
        .language(properties.getLanguage())
        .await();

    if (result.rows.length == 0 || result.rows[0] == null || result.rows[0].elements.length == 0) {
      LOG.error("Couldn't calculate the distance. Result is empty %t", result);
      throw new IllegalStateException("Can't calculate distance with the given input");
    }

    DistanceMatrixElement distanceMatrixElement = result.rows[0].elements[0];
    LOG.debug("Got the results: %t", distanceMatrixElement);

    return new DistanceMatrixResult(distanceMatrixElement);
  }

}
