package cash.super_.platform.service.parkingplus.ticket;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import cash.super_.platform.client.parkingplus.model.PagamentoAutorizadoRequest;
import cash.super_.platform.client.parkingplus.model.PagamentoRequest;
import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.service.pagarme.transactions.models.Item;
import cash.super_.platform.service.pagarme.transactions.models.TransactionRequest;
import cash.super_.platform.service.parkingplus.payment.PagarmePaymentProcessorService;
import cash.super_.platform.service.parkingplus.sales.ParkingPlusParkingSalesCachedProxyService;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger LOG = LoggerFactory.getLogger(ParkingPlusTicketsController.class);

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
  @RequestMapping(value = TICKETS_ENDPOINT + "/{ticket_id}/pay", method = RequestMethod.POST,
      consumes = {"application/json"}, produces = {"application/json"})
  public ResponseEntity<ParkingTicketAuthorizedPaymentStatus> authorizeParkingTicketPayment(
      @RequestHeader("X-Supercash-Tid") String transactionId,
      @RequestHeader("X-Supercash-Uid") String headerUserId,
      @PathVariable("supercash_uid") String userId,
      @PathVariable("ticket_id") String ticketId,
      @RequestBody ParkingTicketPayment paymentRequest)
      throws IOException, InterruptedException {

    isRequestValid(headerUserId, userId);

    // Basic parameters validation
    Preconditions.checkArgument(paymentRequest != null, "The payment request must be provided");

    ParkingTicketAuthorizedPaymentStatus paymentStatus = null;
    String ticketNumber = "";
    if (paymentRequest.getPayTicketRequest() != null) {
      TransactionRequest request = paymentRequest.getPayTicketRequest();
      List<Item> items = request.getItems();
      Preconditions.checkArgument(items != null && items.size() > 0, "At least one item must " +
              "be provided");
      Integer amount = items.get(0).getUnitPrice();
      isTicketAndAmountValid(userId, ticketId, items.get(0).getId(), amount);

      paymentStatus = pagarmePaymentProcessorService.processPayment(userId, paymentRequest.getPayTicketRequest());

    } else if (paymentRequest.getAuthorizedRequest() != null) {
      PagamentoAutorizadoRequest request = paymentRequest.getAuthorizedRequest();
      Integer amount = request.getValor();
      isTicketAndAmountValid(userId, ticketId, request.getNumeroTicket(), amount);

      paymentStatus = paymentAuthService.authorizePayment(userId, paymentRequest.getAuthorizedRequest());

    } else if (paymentRequest.getRequest() != null) {
      PagamentoRequest request = paymentRequest.getRequest();
      Integer amount = request.getValor();
      isTicketAndAmountValid(userId, ticketId, request.getNumeroTicket(), amount);

      paymentStatus = paymentAuthService.authorizePayment(userId, request);

    } else {
      throw new IllegalArgumentException("You must provide a request, an authorizedRequest or a transactionRequest");
    }

    return new ResponseEntity<>(paymentStatus, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }

  /**
   * Retrieve the status of a given ticket for of a given user
   * @param transactionId
   * @param userId
   * @param ticketId
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  @ApiOperation(value = "", nickname = TICKETS_ENDPOINT + "/{ticket_id}")
  @RequestMapping(value = TICKETS_ENDPOINT + "/{ticket_id}", method = RequestMethod.GET, produces = {"application/json"})
  public ResponseEntity<ParkingTicketStatus> getTicketStatus(
      @RequestHeader("X-Supercash-Tid") String transactionId,
      @RequestHeader("X-Supercash-Uid") String headerUserId,
      @PathVariable("supercash_uid") String userId,
      @PathVariable("ticket_id") String ticketId,
      @RequestParam("saleId") Optional<Long> saleId) throws IOException, InterruptedException {

    isRequestValid(headerUserId, userId);

    ParkingTicketStatus parkingTicketStatus = statusService.getStatus(userId, ticketId, saleId);

    LOG.debug("Promoção valor desconto: " + parkingTicketStatus.getStatus().getValorDesconto());

    return new ResponseEntity<>(parkingTicketStatus, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }
}
