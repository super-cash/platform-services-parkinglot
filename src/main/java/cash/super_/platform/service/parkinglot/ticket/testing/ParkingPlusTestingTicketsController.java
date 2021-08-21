package cash.super_.platform.service.parkinglot.ticket.testing;

import cash.super_.platform.service.parkinglot.AbstractController;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("/${cash.super.platform.service.parkinglot.apiVersion}")
@Profile({"dev", "default"})
public class ParkingPlusTestingTicketsController extends AbstractController {

  @Autowired
  protected TestingParkingLotStatusInMemoryRepository testingParkinglotRepo;

  /**
   * Reset the state of the testing parking tickets
   * @return ParkingTicketPaymentsMadeStatus
   */
  @ApiOperation(value = "", nickname = BASE_ENDPOINT + "/tests/reset")
  @RequestMapping(value = BASE_ENDPOINT + "/tests/reset", method = RequestMethod.PUT,
          produces = {MediaType.TEXT_PLAIN_VALUE})
  public ResponseEntity<String> resetTestingParkingTickets(@RequestParam("graceTimeMin") Optional<Integer> gracePeriodMin,
                                                           @RequestParam("priceChangeMin") Optional<Integer> nextPriceInMin) {

    // Just reset the values in memory
    testingParkinglotRepo.resetTestTickets(gracePeriodMin, nextPriceInMin);

    Map<String, String> headers = new HashMap<>();
    TestingParkingLotStatusInMemoryRepository.addTestingHeaders(headers);

    // The call succeeded
    return new ResponseEntity<>("OK", makeDefaultHttpHeaders(headers), HttpStatus.OK);
  }
}