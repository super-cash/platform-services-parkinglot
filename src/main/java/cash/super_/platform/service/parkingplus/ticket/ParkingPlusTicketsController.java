package cash.super_.platform.service.parkingplus.ticket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import cash.super_.platform.service.parkingplus.AbstractController;
import cash.super_.platform.service.parkingplus.model.ParkingTicketAuthorization;
import cash.super_.platform.service.parkingplus.model.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.service.parkingplus.model.ParkingTicketPaymentsMadeStatus;
import cash.super_.platform.service.parkingplus.model.ParkingTicketStatus;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/${cash.super.platform.service.parkingplus.apiVersion}")
public class ParkingPlusTicketsController extends AbstractController {

  /**
   * The endpoint for the tickets status
   */
  private static final String TICKETS_ENDPOINT = BASE_ENDPOINT + "/tickets";

  @Autowired
  private ParkingPlusTicketStatusProxyService statusService;

  @Autowired
  private ParkingPlusTicketPaymentsProxyService paymentsService;

  @Autowired
  private ParkingPlusTicketAuthorizePaymentProxyService paymentAuthService;

  /**
   * Gets the current list of tickets for a given user
   * @param transactionId
   * @param userId
   * @param paginationStart
   * @param paginationLimit
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  @ApiOperation(value = "", nickname = TICKETS_ENDPOINT)
  @RequestMapping(value = TICKETS_ENDPOINT, method = RequestMethod.GET, produces = {"application/json"})
  public ResponseEntity<ParkingTicketPaymentsMadeStatus> getParkingTicketPaymentsStatus(
      @RequestHeader("supercash_tid") String transactionId, 
      @RequestHeader("supercash_uid") String headerUserId,
      @PathVariable("supercash_uid") String userId,
      @RequestParam("page_start") Optional<Integer> paginationStart,
      @RequestParam("page_limit") Optional<Integer> paginationLimit) throws IOException, InterruptedException {

    isRequestValid(headerUserId, userId);

    ParkingTicketPaymentsMadeStatus parkingTicketStatus =
        paymentsService.getPaymentsMade(userId, paginationStart, paginationLimit);

    return new ResponseEntity<>(parkingTicketStatus, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }

  /**
   * Pays a ticket for a given user
   * @param transactionId
   * @param userId
   * @param ticketId
   * @param paymentAuthorization
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  @ApiOperation(value = "", nickname = TICKETS_ENDPOINT)
  @RequestMapping(value = TICKETS_ENDPOINT + "/{ticket_id}/pay", method = RequestMethod.POST,
      consumes = {"application/json"}, produces = {"application/json"})
  public ResponseEntity<ParkingTicketAuthorizedPaymentStatus> authorizeParkingTicketPayment(
      @RequestHeader("supercash_tid") String transactionId,
      @RequestHeader("supercash_uid") String headerUserId,
      @PathVariable("supercash_uid") String userId,
      @PathVariable("ticket_id") String ticketId,
      @RequestBody ParkingTicketAuthorization paymentAuthorization)
      throws IOException, InterruptedException {

    isRequestValid(headerUserId, userId);

    if (!ticketId.equals(paymentAuthorization.getRequest().getNumeroTicket())) {
      throw new IllegalArgumentException("The ticket number in the body must be the same as path 'numeroTicket'!");
    }

    ParkingTicketAuthorizedPaymentStatus paymentStatus =
        paymentAuthService.authorizePayment(userId, paymentAuthorization);

    return new ResponseEntity<>(paymentStatus, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }

  /**
   * Retrieve the status of a given ticket for of a given user
   * @param transactionId
   * @param userId
   * @param ticketId
   * @param parkingTicket
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  // The name in swagger metadata is coming as "operationId":"distancematrixUsingPOST"
  // https://stackoverflow.com/questions/38821763/how-to-customize-the-value-of-operationid-generated-in-api-spec-with-swagger/59044919#59044919
  @ApiOperation(value = "", nickname = TICKETS_ENDPOINT)
  @RequestMapping(value = TICKETS_ENDPOINT + "/{ticket_id}", method = RequestMethod.GET, produces = {"application/json"})
  public ResponseEntity<ParkingTicketStatus> getTicketStatus(
      @RequestHeader("supercash_tid") String transactionId,
      @RequestHeader("supercash_uid") String headerUserId,
      @PathVariable("supercash_uid") String userId,
      @PathVariable("ticket_id") String ticketId,
      @RequestParam("saleId") Optional<Long> saleId) throws IOException, InterruptedException {

    isRequestValid(headerUserId, userId);

    ParkingTicketStatus parkingTicketStatus = statusService.getStatus(userId, ticketId, saleId);

    return new ResponseEntity<>(parkingTicketStatus, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }
}
