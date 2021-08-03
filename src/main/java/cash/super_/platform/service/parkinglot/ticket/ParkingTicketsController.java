package cash.super_.platform.service.parkinglot.ticket;

import cash.super_.platform.service.parkinglot.AbstractController;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicket;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/${cash.super.platform.service.parkinglot.apiVersion}")
public class ParkingTicketsController extends AbstractController {

  @Autowired
  protected ParkingPlusTicketStatusProxyService statusService;

  @Autowired
  protected ParkinglotTicketsService parkinglotTicketsService;

  /**
   * Gets the current list of tickets for a given user
   * @param createdAt
   * @return ParkingTicketPaymentsMadeStatus
   */
  @ApiOperation(value = "", nickname = TICKETS_ENDPOINT)
  @RequestMapping(value = TICKETS_ENDPOINT, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<List<ParkinglotTicket>> getParkingTicketsForUser(
      @RequestParam("created_at") Optional<Long> createdAt,
      @RequestParam("page_offset") Optional<Integer> pageOffset,
      @RequestParam("page_limit") Optional<Integer> pageLimit) {

    List<ParkinglotTicket> parkingTickets = parkinglotTicketsService.retrieveTickets(createdAt, pageOffset, pageLimit);
    Map<String, String> paginationTotals = new HashMap<>();
    paginationTotals.put("X-Supercash-Pagination-Total", String.valueOf(parkingTickets.size()));

    return new ResponseEntity<>(parkingTickets, makeDefaultHttpHeaders(paginationTotals), HttpStatus.OK);
  }

}
