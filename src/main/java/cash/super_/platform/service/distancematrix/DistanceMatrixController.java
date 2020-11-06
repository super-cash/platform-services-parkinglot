package cash.super_.platform.service.distancematrix;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.google.maps.errors.ApiException;
import cash.super_.platform.service.DistanceMatrixApplication;
import cash.super_.platform.service.distancematrix.model.DistanceMatrixAddresses;
import cash.super_.platform.service.distancematrix.model.DistanceMatrixResult;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/${cash.super.platform.service.distancematrix.apiVersion}")
public class DistanceMatrixController extends AbstractController {

  private static final Logger LOG = LoggerFactory.getLogger(DistanceMatrixApplication.class);

  /**
   * Where the call will come through
   */
  public static final String BASE_ENDPOINT = "/distancematrix";

  @Autowired
  private DistanceMatrixGoogleGeoAPICachedProxyService service;

  // The name in swagger metadata is coming as "operationId":"distancematrixUsingPOST"
  // https://stackoverflow.com/questions/38821763/how-to-customize-the-value-of-operationid-generated-in-api-spec-with-swagger/59044919#59044919
  @ApiOperation(value = "", nickname = BASE_ENDPOINT)
  @RequestMapping(value = BASE_ENDPOINT, method = RequestMethod.POST, consumes = {"application/json"},
      produces = {"application/json"})
  public ResponseEntity<DistanceMatrixResult> calculateDistance(
      @RequestBody DistanceMatrixAddresses distanceMatrixAddresses,
      @RequestHeader("supercash_tid") Optional<String> transactionId,
      @RequestHeader("supercash_cid") Optional<String> clientId)
      throws IOException, ApiException, InterruptedException {

    DistanceMatrixResult result = service.getDriveDistance(distanceMatrixAddresses);
    LOG.info("Distance of {} is {} ", distanceMatrixAddresses, result);

    // Propagate the headers back to the client
    Map<String, String> responseHeaders = new HashMap<>();
    if (transactionId.isPresent()) {
      responseHeaders.put("supercash_tid", transactionId.get());
    }
    if (transactionId.isPresent()) {
      responseHeaders.put("supercash_cid", clientId.get());
    }

    return new ResponseEntity<>(result, makeDefaultHttpHeaders(responseHeaders), HttpStatus.OK);
  }
}
