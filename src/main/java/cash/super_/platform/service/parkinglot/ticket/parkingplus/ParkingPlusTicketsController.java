package cash.super_.platform.service.parkinglot.ticket.parkingplus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import cash.super_.platform.model.parkinglot.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.model.parkinglot.ParkingTicketPayment;
import cash.super_.platform.model.parkinglot.ParkingTicketStatus;
import cash.super_.platform.service.parkinglot.ticket.parkingplus.ParkingPlusTicketAuthorizePaymentProxyService;
import cash.super_.platform.service.parkinglot.ticket.parkingplus.ParkingPlusTicketStatusProxyService;
import cash.super_.platform.service.parkinglot.ticket.testing.TestingParkingLotStatusInMemoryRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import cash.super_.platform.service.parkinglot.AbstractController;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(tags="ParkinglotsServiceClient")
@RequestMapping("/${cash.super.platform.service.parkinglot.apiVersion}")
public class ParkingPlusTicketsController extends AbstractController {

  /**
   * The endpoint for the tickets status
   */
  protected static final String TICKETS_ENDPOINT = BASE_ENDPOINT + "/tickets";

  @Autowired
  protected ParkingPlusTicketStatusProxyService statusService;

  @Autowired
  protected ParkingPlusTicketAuthorizePaymentProxyService paymentAuthService;

  /**
   * Retrieve the status of a given ticket for of a given user
   * @param ticketNumber the ticket number
   * @param scanned if the ticket was scanned
   * @return ParkingTicketStatus
   */
  @ApiOperation(nickname = "retrieve", value = "Retrieves a ticket from the system.")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "If the ticket exists. (Testing tickets: free-112233445566, existing-010101010101, exit-111111000000"),
          @ApiResponse(code = 400, message = "When missing or with incorrect parameters"),
          @ApiResponse(code = 404, message = "When the storeId or ticketId does not exist"),
          @ApiResponse(code = 500, message = "Unidentified errors in the server"),
          @ApiResponse(code = 501, message = "If the selected type is not implemented"),
          @ApiResponse(code = 503, message = "When the underlying service in use is not reachable"),
  })
  @GetMapping(value = {TICKETS_ENDPOINT + "/{ticket_number}"}, produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ParkingTicketStatus> getTicketStatus(
          @PathVariable("parkinglot_id") Long parkinglotId,
          @PathVariable("ticket_number") String ticketNumber,
          @RequestParam("scanned") Optional<Boolean> scanned,
          @RequestHeader("X-Supercash-Tid") String transactionId,
          @RequestHeader("X-Supercash-Marketplace-Id") Long marketplaceId,
          @RequestHeader("X-Supercash-Store-Id") Long storeId,
          @RequestHeader("X-Supercash-App-Version") Double appVersion,
          @RequestHeader("X-Supercash-Uid") Long userId) {

    // validate the request context
    validateSupercashContext(transactionId, marketplaceId, storeId, userId, appVersion, parkinglotId);

    boolean wasScanned = scanned.isPresent() ? scanned.get() : false;
    ParkingTicketStatus parkingTicketStatus = statusService.getStatus(parkinglotId, ticketNumber, wasScanned);

    Map<String, String> headers = new HashMap<>();
    TestingParkingLotStatusInMemoryRepository.addTestingHeaders(headers);

    return new ResponseEntity<>(parkingTicketStatus, makeDefaultHttpHeaders(headers), HttpStatus.OK);
  }

  /**
   * Pays a ticket for a given user using WPS infra
   * @param ticketNumber
   * @param paymentRequest
   * @return ParkingTicketAuthorizedPaymentStatus
   */
  @ApiOperation(nickname = "pay", value = "Pays a given ticket from the system")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "If the ticket exists"),
          @ApiResponse(code = 400, message = "When missing or with incorrect parameters"),
          @ApiResponse(code = 404, message = "When the storeId or ticketId does not exist"),
          @ApiResponse(code = 500, message = "Unidentified errors in the server"),
          @ApiResponse(code = 501, message = "If the selected type is not implemented"),
          @ApiResponse(code = 503, message = "When the underlying service in use is not reachable"),
  })
  @PostMapping(value = TICKETS_ENDPOINT + "/{ticket_number}/pay",
          consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ParkingTicketAuthorizedPaymentStatus> authorizeParkingTicketPayment(
          @PathVariable("parkinglot_id") Long parkinglotId,
          @PathVariable("ticket_number") String ticketNumber,
          @RequestBody ParkingTicketPayment paymentRequest,
          @RequestHeader("X-Supercash-Tid") String transactionId,
          @RequestHeader("X-Supercash-Marketplace-Id") Long marketplaceId,
          @RequestHeader("X-Supercash-Store-Id") Long storeId,
          @RequestHeader("X-Supercash-App-Version") Double appVersion,
          @RequestHeader("X-Supercash-Uid") Long userId) {

    // The context is added by the supercashSecurityInterceptor
    validateSupercashContext(transactionId, marketplaceId, storeId, userId, appVersion, parkinglotId);

    ParkingTicketAuthorizedPaymentStatus paymentStatus = paymentAuthService.process(parkinglotId,
            paymentRequest, ticketNumber);

    Map<String, String> headers = new HashMap<>();
    TestingParkingLotStatusInMemoryRepository.addTestingHeaders(headers);

    return new ResponseEntity<>(paymentStatus, makeDefaultHttpHeaders(headers), HttpStatus.OK);
  }

}
