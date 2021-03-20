package cash.super_.platform.service.parkingplus.payment;

import cash.super_.platform.service.parkingplus.AbstractController;
import cash.super_.platform.service.parkingplus.model.ParkingPlusPaymentGracePeriod;
import cash.super_.platform.service.parkingplus.model.ParkingPlusPaymentServiceFee;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.HashMap;

@Controller
@RequestMapping("/${cash.super.platform.service.parkingplus.apiVersion}")
public class ParkingPlusPaymentController extends AbstractController {

  private static final Logger LOG = LoggerFactory.getLogger(ParkingPlusPaymentController.class);

  /**
   * The endpoint for the authorization
   */
  private static final String PAYMENT_ENDPOINT = BASE_ENDPOINT + "/payment";

  @ApiOperation(value = "", nickname = PAYMENT_ENDPOINT + "/servicefee")
  @RequestMapping(value = PAYMENT_ENDPOINT + "/servicefee", method = RequestMethod.GET,
          produces = {"application/json"})
  public ResponseEntity<ParkingPlusPaymentServiceFee> getPaymentServiceFee(
      @RequestHeader("X-Supercash-Tid") String transactionId,
      @RequestHeader("X-Supercash-Uid") String headerUserId,
      @PathVariable("supercash_uid") String userId) throws IOException, InterruptedException {

    isRequestValid(headerUserId, userId);

    ParkingPlusPaymentServiceFee pppsf = new ParkingPlusPaymentServiceFee(properties.getOurFee());

    return new ResponseEntity<>(pppsf,
            makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }

  @ApiOperation(value = "", nickname = PAYMENT_ENDPOINT + "/graceperiod")
  @RequestMapping(value = PAYMENT_ENDPOINT + "/graceperiod", method = RequestMethod.GET,
          produces = {"application/json"})
  public ResponseEntity<ParkingPlusPaymentGracePeriod> getPaymentGracePeriod(
          @RequestHeader("X-Supercash-Tid") String transactionId,
          @RequestHeader("X-Supercash-Uid") String headerUserId,
          @PathVariable("supercash_uid") String userId) throws IOException, InterruptedException {

    isRequestValid(headerUserId, userId);

    ParkingPlusPaymentGracePeriod pppgp = new ParkingPlusPaymentGracePeriod(properties.getGracePeriod());

    return new ResponseEntity<>(pppgp,
            makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }
}
