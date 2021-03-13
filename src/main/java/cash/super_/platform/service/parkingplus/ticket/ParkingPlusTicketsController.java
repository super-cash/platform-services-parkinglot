package cash.super_.platform.service.parkingplus.ticket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import cash.super_.platform.client.parkingplus.model.PagamentoAutorizadoRequest;
import cash.super_.platform.service.pagarme.transactions.models.Transaction;
import cash.super_.platform.service.pagarme.transactions.models.TransactionRequest;
import cash.super_.platform.service.pagarme.transactions.models.TransactionResponseSummary;
import cash.super_.platform.service.parkingplus.payment.PagarmePaymentProcessorService;
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
import cash.super_.platform.service.parkingplus.model.ParkingTicketPayment;
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

  @Autowired
  PagarmePaymentProcessorService pagarmePaymentProcessorService;

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
      @RequestHeader("X-Supercash-Tid") String transactionId, 
      @RequestHeader("X-Supercash-Uid") String headerUserId,
      @PathVariable("supercash_uid") String userId,
      @RequestParam("page_start") Optional<Integer> paginationStart,
      @RequestParam("page_limit") Optional<Integer> paginationLimit) throws IOException, InterruptedException {

    isRequestValid(headerUserId, userId);

    ParkingTicketPaymentsMadeStatus parkingTicketStatus =
        paymentsService.getPaymentsMade(userId, paginationStart, paginationLimit);

    return new ResponseEntity<>(parkingTicketStatus, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }

  /**
   * Pays a ticket for a given user using WPS infra
   * @param transactionId
   * @param userId
   * @param ticketId
   * @param paymentAuthorization
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  @ApiOperation(value = "", nickname = TICKETS_ENDPOINT)
  @RequestMapping(value = TICKETS_ENDPOINT + "/{ticket_id}/payWithWPS", method = RequestMethod.POST,
      consumes = {"application/json"}, produces = {"application/json"})
  public ResponseEntity<ParkingTicketAuthorizedPaymentStatus> authorizeParkingTicketPayment(
      @RequestHeader("X-Supercash-Tid") String transactionId,
      @RequestHeader("X-Supercash-Uid") String headerUserId,
      @PathVariable("supercash_uid") String userId,
      @PathVariable("ticket_id") String ticketId,
      @RequestBody ParkingTicketPayment paymentRequest)
      throws IOException, InterruptedException {

    isRequestValid(headerUserId, userId);

    ParkingTicketAuthorizedPaymentStatus paymentStatus = null;
    if (paymentRequest.getAuthorizedRequest() != null) {
      if (!ticketId.equals(paymentRequest.getAuthorizedRequest().getNumeroTicket())) {
        throw new IllegalArgumentException("The authorized ticket number in body is different than URL 'numeroTicket'");
      }

      paymentStatus = paymentAuthService.authorizePayment(userId, paymentRequest.getAuthorizedRequest());

    } else if (paymentRequest.getRequest() != null) {
      if (!ticketId.equals(paymentRequest.getRequest().getNumeroTicket())) {
        throw new IllegalArgumentException("The ticket number in the body must is different than URL 'numeroTicket'");
      }

      paymentStatus = paymentAuthService.authorizePayment(userId, paymentRequest.getRequest());

    } else {
      throw new IllegalArgumentException("You must provide either the request or authorizedRequest");
    }

    return new ResponseEntity<>(paymentStatus, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }

  /**
   * Pays a ticket for a given user using Supercash
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
          @RequestHeader("X-Supercash-Tid") String transactionId,
          @RequestHeader("X-Supercash-Uid") String headerUserId,
          @PathVariable("supercash_uid") String userId,
          @PathVariable("ticket_id") String ticketId,
          @RequestBody TransactionRequest paymentRequest)
          throws IOException, InterruptedException {

    isRequestValid(headerUserId, userId);

    TransactionResponseSummary transactionResponse = pagarmePaymentProcessorService.processPayment(userId,
            paymentRequest);

    ParkingTicketAuthorizedPaymentStatus paymentStatus = null;
    if (transactionResponse.getStatus() == Transaction.Status.PAID) {
//      paymentStatus = paymentAuthService.authorizePaymentWithSupercash(userId, paymentRequest, transactionResponse);
      System.out.println("Ticket " + ticketId + " paid successfully.");
    }

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
      @RequestHeader("X-Supercash-Tid") String transactionId,
      @RequestHeader("X-Supercash-Uid") String headerUserId,
      @PathVariable("supercash_uid") String userId,
      @PathVariable("ticket_id") String ticketId,
      @RequestParam("saleId") Optional<Long> saleId) throws IOException, InterruptedException {

    isRequestValid(headerUserId, userId);

    ParkingTicketStatus parkingTicketStatus = statusService.getStatus(userId, ticketId, saleId);

    return new ResponseEntity<>(parkingTicketStatus, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }
}
