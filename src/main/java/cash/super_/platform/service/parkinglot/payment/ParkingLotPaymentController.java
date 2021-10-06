package cash.super_.platform.service.parkinglot.payment;

import cash.super_.platform.service.parkinglot.AbstractController;
import cash.super_.platform.model.parkinglot.ParkingPlusPaymentGracePeriod;
import cash.super_.platform.model.parkinglot.ParkingPlusPaymentServiceFee;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

@Controller
@Api(tags="ParkinglotsServiceClient")
@RequestMapping("/${cash.super.platform.service.parkinglot.apiVersion}")
public class ParkingLotPaymentController extends AbstractController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ParkingLotPaymentController.class);

  /**
   * The endpoint for the authorization
   */
  private static final String PAYMENT_ENDPOINT = BASE_ENDPOINT + "/payments";

    /**
     * Retrieve the status of a given ticket for of a given user
     * @param parkinglotId the the parkinglot Id
     * @return ParkingTicketStatus
     */
    @ApiOperation(nickname = "getServiceFee", value = "Retrieves a ticket from the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If the ticket exists. (Testing tickets: free-112233445566, existing-010101010101, exit-111111000000"),
            @ApiResponse(code = 400, message = "When missing or with incorrect parameters"),
            @ApiResponse(code = 404, message = "When the storeId or ticketId does not exist"),
            @ApiResponse(code = 500, message = "Unidentified errors in the server"),
            @ApiResponse(code = 501, message = "If the selected type is not implemented"),
            @ApiResponse(code = 503, message = "When the underlying service in use is not reachable"),
    })
  @GetMapping(value = PAYMENT_ENDPOINT + "/servicefee", produces = {"application/json"})
  public ResponseEntity<ParkingPlusPaymentServiceFee> getPaymentServiceFee(
      @PathVariable("parkinglot_id") Long parkinglotId) throws IOException, InterruptedException {

    LOGGER.debug("Retrieving the serviceFee for the parkinglot={}", parkinglotId);
    ParkingPlusPaymentServiceFee pppsf = new ParkingPlusPaymentServiceFee(parkinglotId, properties.getOurFee());

    return new ResponseEntity<>(pppsf, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }

  @ApiOperation(nickname = "getGracePeriod", value = "Retrieves the grace period for the given parkinglot.")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "If the given parkinglot exists and has graceperiod information"),
          @ApiResponse(code = 400, message = "When missing or with incorrect parameters"),
          @ApiResponse(code = 404, message = "When the the given parkinglotID does not exist"),
          @ApiResponse(code = 500, message = "Unidentified errors in the server"),
          @ApiResponse(code = 501, message = "If the selected type is not implemented"),
          @ApiResponse(code = 503, message = "When the underlying service in use is not reachable"),
  })
  @GetMapping(value = PAYMENT_ENDPOINT + "/graceperiod", produces = {"application/json"})
  public ResponseEntity<ParkingPlusPaymentGracePeriod> getPaymentGracePeriod(
          @PathVariable("parkinglot_id") Long parkinglotId) {

      LOGGER.debug("Retrieving the graceperiod for the parkinglot={}", parkinglotId);
      ParkingPlusPaymentGracePeriod pppgp = new ParkingPlusPaymentGracePeriod(parkinglotId, properties.getGracePeriodInMinutes());

    return new ResponseEntity<>(pppgp, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }
}
