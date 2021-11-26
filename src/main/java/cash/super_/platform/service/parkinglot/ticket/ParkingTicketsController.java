package cash.super_.platform.service.parkinglot.ticket;

import cash.super_.platform.service.parkinglot.AbstractController;
import cash.super_.platform.model.parkinglot.ParkinglotTicket;
import cash.super_.platform.service.parkinglot.ticket.parkingplus.ParkingPlusTicketStatusProxyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags="ParkinglotsServiceClient")
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
  @ApiOperation(nickname = "list", value = "Retrieves the list of parkinglot tickets for the user stored in our system")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "If there is a list of 0 or more tickets for the userId"),
          @ApiResponse(code = 400, message = "When missing or with incorrect parameters"),
          @ApiResponse(code = 404, message = "When parkinglot ID does not exist"),
          @ApiResponse(code = 500, message = "Unidentified errors in the server"),
          @ApiResponse(code = 501, message = "If the selected type is not implemented"),
          @ApiResponse(code = 503, message = "When the underlying service in use is not reachable"),
  })
  @GetMapping(value = TICKETS_ENDPOINT, produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<List<ParkinglotTicket>> getParkingTicketsForUser(
          @PathVariable("parkinglot_id") Long parkinglotId,
          @RequestParam("created_at") Optional<Long> createdAt,
          @RequestParam("page_offset") Optional<Integer> pageOffset,
          @RequestParam("page_limit") Optional<Integer> pageLimit,
          @RequestParam("ticket_number") Optional<String> ticketNumber,
          @RequestHeader("X-Supercash-Tid") String transactionId,
          @RequestHeader("X-Supercash-Marketplace-Id") Long marketplaceId,
          @RequestHeader("X-Supercash-Store-Id") Long storeId,
          @RequestHeader("X-Supercash-App-Version") Double appVersion,
          @RequestHeader("X-Supercash-Uid") Long userId) {

    // The context is added by the supercashSecurityInterceptor
    validateSupercashContext(transactionId, marketplaceId, storeId, userId, appVersion, parkinglotId);

    List<ParkinglotTicket> parkingTickets = parkinglotTicketsService.retrieveTickets(parkinglotId, ticketNumber,
            createdAt, pageOffset, pageLimit);
    Map<String, String> paginationTotals = new HashMap<>();

    int numberOfTickets = parkingTickets != null ? parkingTickets.size() : 0;
    paginationTotals.put("X-Supercash-Pagination-Total", String.valueOf(numberOfTickets));

    return new ResponseEntity<>(parkingTickets, makeDefaultHttpHeaders(paginationTotals), HttpStatus.OK);
  }

}
