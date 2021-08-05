package cash.super_.platform.service.parkinglot.ticket;

import cash.super_.platform.service.parkinglot.AbstractController;
import cash.super_.platform.service.parkinglot.repository.TestingParkingLotStatusInMemoryRepository;
import cash.super_.platform.utils.DateTimeUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    Map<String, String> headers = new HashMap<>();
    LocalDateTime nowDateTime = DateTimeUtil.getLocalDateTime(DateTimeUtil.getNow());

    int gracePeriodInMinutes = TestingParkingLotStatusInMemoryRepository.GRACE_PERIOD_DURING_TESTING;
    headers.put("X-Supercash-Test-Grace-Period-Timeout",
            String.valueOf(DateTimeUtil.getMillis(nowDateTime.plusMinutes(gracePeriodInMinutes))));

    int priceChangeInMinutes = TestingParkingLotStatusInMemoryRepository.PRICE_CHANGE_IN_MINUTES;
    headers.put("X-Supercash-Test-Price-Change-Timeout",
            String.valueOf(DateTimeUtil.getMillis(nowDateTime.plusMinutes(priceChangeInMinutes))));

    // The call succeeded
    return new ResponseEntity<>("OK", makeDefaultHttpHeaders(headers), HttpStatus.OK);
  }
}
