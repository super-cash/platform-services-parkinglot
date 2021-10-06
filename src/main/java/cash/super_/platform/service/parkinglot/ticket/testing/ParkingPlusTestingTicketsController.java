package cash.super_.platform.service.parkinglot.ticket.testing;

import cash.super_.platform.service.parkinglot.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@Api(tags="ParkinglotsServiceClient")
@RequestMapping("/${cash.super.platform.service.parkinglot.apiVersion}")
@Profile({"dev", "default"})
public class ParkingPlusTestingTicketsController extends AbstractController {

  @Autowired
  protected TestingParkingLotStatusInMemoryRepository testingParkinglotRepo;

  /**
   * Reset the state of the testing parking tickets
   * @return ParkingTicketPaymentsMadeStatus
   */
  @ApiOperation(nickname = "restTestTickets", value = "Resets the state of all testing tickets")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Resets the in-memory value."),
          @ApiResponse(code = 400, message = "When missing or with incorrect parameters"),
          @ApiResponse(code = 404, message = "When the storeId or ticketId does not exist"),
          @ApiResponse(code = 500, message = "Unidentified errors in the server"),
          @ApiResponse(code = 501, message = "If the selected type is not implemented"),
          @ApiResponse(code = 503, message = "When the underlying service in use is not reachable"),
  })
  @PutMapping(value = BASE_ENDPOINT + "/tests/reset", produces = {MediaType.TEXT_PLAIN_VALUE})
  public ResponseEntity<String> resetTestingParkingTickets(
          @PathVariable("parkinglot_id") Long parkinglotId,
          @RequestParam("graceTimeMin") Optional<Integer> gracePeriodMin,
          @RequestParam("priceChangeMin") Optional<Integer> nextPriceInMin) {

    // Just reset the values in memory
    testingParkinglotRepo.resetTestTickets(parkinglotId, gracePeriodMin, nextPriceInMin);

    Map<String, String> headers = new HashMap<>();
    TestingParkingLotStatusInMemoryRepository.addTestingHeaders(headers);

    // The call succeeded
    return new ResponseEntity<>("OK", makeDefaultHttpHeaders(headers), HttpStatus.OK);
  }
}