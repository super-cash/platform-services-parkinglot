package cash.super_.platform.service.parkinglot.ticket;

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

@Controller
@RequestMapping("/${cash.super.platform.service.parkinglot.apiVersion}")
@Profile({"dev", "default"})
public class ParkingPlusTestingTicketsController extends AbstractController {

  @Autowired
  protected ParkingPlusTicketStatusProxyService statusService;

  /**
   * Reset the state of the testing parking tickets
   * @return ParkingTicketPaymentsMadeStatus
   */
  @ApiOperation(value = "", nickname = BASE_ENDPOINT + "/tests/reset")
  @RequestMapping(value = BASE_ENDPOINT + "/tests/reset", method = RequestMethod.PUT, produces = {MediaType.TEXT_PLAIN_VALUE})
  public ResponseEntity<String> resetTestingParkingTickets() {

    // Just reset the vsalues in memory
    statusService.resetTestTickets();

    // The call succeeded
    return new ResponseEntity<>("OK", makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }
}
