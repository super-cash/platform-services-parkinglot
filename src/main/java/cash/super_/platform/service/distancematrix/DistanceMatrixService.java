package cash.super_.platform.service.distancematrix;

import java.io.IOException;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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

/**
 * The Service as also a cache loader, as a separate class would not be different. Too little to be
 * added in isolation. Based on https://www.baeldung.com/guava-cache.
 *
 * @author marcellodesales
 *
 */
@Service
public class DistanceMatrixService extends CacheLoader<DistanceMatrixAddresses, DistanceMatrixResult> {

  private static final Logger LOG = LoggerFactory.getLogger(DistanceMatrixService.class);

  @Autowired
  private DistanceMatrixProperties properties;

  /**
   * The API is very expensive to be built
   */
  private GeoApiContext geoApi;

  /**
   * The Cache of results based on the input https://www.baeldung.com/guava-cache. It has an eviction
   * policy of x minutes for older results to be removed.
   */
  private LoadingCache<DistanceMatrixAddresses, DistanceMatrixResult> cache;

  @PostConstruct
  public void postConstruct() {
    // this call if extremely expensive and must be cached
    String googleApiToken = properties.getGoogleMapsApiToken();
    LOG.info("Bootstrapping the Google Geo API with token: {}", StringUtil.obsfucate(googleApiToken));
    geoApi = new GeoApiContext.Builder().apiKey(googleApiToken).build();
    LOG.info("Initialized Google Geo API");

    // If we need to cache locations that are frequently searched, we can add them here
    LOG.info("Bootstrapping the Results Cache; eviction time of {} {}", properties.getResultsCacheDuration(),
        properties.getResultsCacheTimeUnit());
    cache = CacheBuilder.newBuilder()
        .expireAfterAccess(properties.getResultsCacheDuration(), properties.getResultsCacheTimeUnit()).build(this);
    LOG.info("Initialized the Results Cache");
  }

  public DistanceMatrixResult getDriveDistance(DistanceMatrixAddresses addresses)
      throws ApiException, InterruptedException, IOException {

    LOG.debug("Got the requested addresses for calculation: {}", addresses);

    // Verify the input of addresses
    Preconditions.checkArgument(addresses != null, "The addresses must be provided");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(addresses.getOriginAddress()), "The origin must be provided");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(addresses.getDestinationAddress()),
        "The destination must be provided");

    DistanceMatrixResult distanceResult = cache.getUnchecked(addresses);
    return distanceResult;
  }

  /**
   * Loads a given addresses key when it is NOT in cache.
   */
  @Override
  public DistanceMatrixResult load(DistanceMatrixAddresses addresses)
      throws ApiException, InterruptedException, IOException {
    LOG.info("Addresses not in cache: {}", addresses);

    DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(geoApi);

    DistanceMatrix calculationResult = req.origins(addresses.getOriginAddress())
          .destinations(addresses.getDestinationAddress())
          .mode(TravelMode.DRIVING)
          .avoid(RouteRestriction.TOLLS)
          .language(properties.getLanguage())
          .await();

    if (calculationResult.rows.length == 0 || calculationResult.rows[0] == null
        || calculationResult.rows[0].elements.length == 0) {
      LOG.error("Couldn't calculate the distance. Result is empty: {}", calculationResult);
      throw new IllegalStateException("Can't calculate distance with the given input");
    }

    DistanceMatrixElement distanceMatrixElement = calculationResult.rows[0].elements[0];

    DistanceMatrixResult distanceResult = new DistanceMatrixResult(distanceMatrixElement);
    LOG.debug("Caching the distance of {} as {}", addresses, distanceResult);
    return distanceResult;
  }

}
