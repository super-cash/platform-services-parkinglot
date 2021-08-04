package cash.super_.platform.service.parkinglot.ticket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import cash.super_.platform.service.parkinglot.model.*;
import cash.super_.platform.service.parkinglot.model.ParkingTicketStatus;
import cash.super_.platform.service.parkinglot.repository.ParkinglotTicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import cash.super_.platform.service.parkinglot.AbstractController;
import io.swagger.annotations.ApiOperation;

@Controller
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
  @ApiOperation(value = "", nickname = TICKETS_ENDPOINT + "/{ticket_number}")
  @RequestMapping(value = TICKETS_ENDPOINT + "/{ticket_number}", method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ParkingTicketStatus> getTicketStatus(@PathVariable("ticket_number") String ticketNumber,
          @RequestParam("scanned") Optional<Boolean> scanned) {

    boolean wasScanned = scanned.isPresent() ? scanned.get() : false;
    ParkingTicketStatus parkingTicketStatus = statusService.getStatus(ticketNumber, wasScanned);

    Map<String, String> headers = new HashMap<>();
    if (statusService.isTicketForTesting(ticketNumber)) {
      headers.put("X-Supercash-Test", "true");
    }

    return new ResponseEntity<>(parkingTicketStatus, makeDefaultHttpHeaders(headers), HttpStatus.OK);
  }

  /**
   * Pays a ticket for a given user using WPS infra
   * @param ticketNumber
   * @param paymentRequest
   * @return ParkingTicketAuthorizedPaymentStatus
   */
  @ApiOperation(value = "", nickname = TICKETS_ENDPOINT + "/{ticket_number}/pay")
  @RequestMapping(value = TICKETS_ENDPOINT + "/{ticket_number}/pay", method = RequestMethod.POST,
      consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ParkingTicketAuthorizedPaymentStatus> authorizeParkingTicketPayment(
          @PathVariable("ticket_number") String ticketNumber,
      @RequestBody ParkingTicketPayment paymentRequest) {

    ParkingTicketAuthorizedPaymentStatus paymentStatus = paymentAuthService.process(paymentRequest, ticketNumber);

    Map<String, String> headers = new HashMap<>();
    if (statusService.isTicketForTesting(ticketNumber)) {
      headers.put("X-Supercash-Test", "true");
    }

    return new ResponseEntity<>(paymentStatus, makeDefaultHttpHeaders(headers), HttpStatus.OK);
  }

}
