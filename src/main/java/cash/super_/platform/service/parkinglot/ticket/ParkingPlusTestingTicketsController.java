package cash.super_.platform.service.parkinglot.ticket;

import cash.super_.platform.service.parkinglot.AbstractController;
import cash.super_.platform.service.parkinglot.model.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.service.parkinglot.model.ParkingTicketPayment;
import cash.super_.platform.service.parkinglot.model.ParkingTicketPaymentsMadeStatus;
import cash.super_.platform.service.parkinglot.model.ParkingTicketStatus;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@Controller
@RequestMapping("/${cash.super.platform.service.parkinglot.apiVersion}")
@Profile({"dev", "default"})
public class ParkingPlusTestingTicketsController extends AbstractController {

  private static final Logger LOG = LoggerFactory.getLogger(ParkingPlusTestingTicketsController.class);

  @Autowired
  protected ParkingPlusTicketStatusProxyService statusService;

  /**
   * Reset the state of the testing parking tickets
   * @param transactionId
   * @param userId
   * @return ParkingTicketPaymentsMadeStatus
   */
  @ApiOperation(value = "", nickname = BASE_ENDPOINT + "/tests/reset")
  @RequestMapping(value = BASE_ENDPOINT + "/tests/reset", method = RequestMethod.PUT, produces = {MediaType.TEXT_PLAIN_VALUE})
  public ResponseEntity<String> resetTestingParkingTickets(
          @RequestHeader("X-Supercash-Tid") String transactionId,
          @RequestHeader("X-Supercash-Uid") String userId,
          @RequestHeader("X-Supercash-MarketplaceId") String marketplaceId) {

    userId =  properties.getUdidPrefix() + "-" + marketplaceId + "-" + userId;
    statusService.resetTestTickets(transactionId, marketplaceId, userId);
    // The call succeeded
    return new ResponseEntity<>("OK", makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }
}
