package cash.super_.platform.service.parkinglot.ticket;

import java.util.HashMap;
import java.util.Optional;

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
import cash.super_.platform.service.parkinglot.AbstractController;
import cash.super_.platform.service.parkinglot.model.ParkingTicketPayment;
import cash.super_.platform.service.parkinglot.model.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.service.parkinglot.model.ParkingTicketPaymentsMadeStatus;
import cash.super_.platform.service.parkinglot.model.ParkingTicketStatus;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/${cash.super.platform.service.parkinglot.apiVersion}")
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

  /**
   * Gets the current list of tickets for a given user
   * @param transactionId
   * @param userId
   * @param paginationStart
   * @param paginationLimit
   * @return ParkingTicketPaymentsMadeStatus
   */
  @ApiOperation(value = "", nickname = TICKETS_ENDPOINT)
  @RequestMapping(value = TICKETS_ENDPOINT, method = RequestMethod.GET, produces = {"application/json"})
  public ResponseEntity<ParkingTicketPaymentsMadeStatus> getParkingTicketPaymentsStatus(
      @RequestHeader("X-Supercash-Tid") String transactionId, 
      @RequestHeader("X-Supercash-Uid") String userId,
      @RequestHeader("X-Supercash-MarketplaceId") String marketplaceId,
      @RequestHeader("X-Supercash-StoreId") String storeId,
      @RequestParam("page_start") Optional<Integer> paginationStart,
      @RequestParam("page_limit") Optional<Integer> paginationLimit) {

    ParkingTicketPaymentsMadeStatus parkingTicketStatus =
        paymentsService.getPaymentsMade(userId, paginationStart, paginationLimit);

    return new ResponseEntity<>(parkingTicketStatus, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }

  /**
   * Pays a ticket for a given user using WPS infra
   * @param transactionId
   * @param userId
   * @param ticketId
   * @param paymentRequest
   * @return ParkingTicketAuthorizedPaymentStatus
   */
  @ApiOperation(value = "", nickname = TICKETS_ENDPOINT)
  @RequestMapping(value = TICKETS_ENDPOINT + "/{ticket_id}/pay", method = RequestMethod.POST,
      consumes = {"application/json"}, produces = {"application/json"})
  public ResponseEntity<ParkingTicketAuthorizedPaymentStatus> authorizeParkingTicketPayment(
      @RequestHeader("X-Supercash-Tid") String transactionId,
      @RequestHeader("X-Supercash-Uid") String userId,
      @RequestHeader("X-Supercash-MarketplaceId") String marketplaceId,
      @RequestHeader("X-Supercash-StoreId") String storeId,
      @PathVariable("ticket_id") String ticketId,
      @RequestBody ParkingTicketPayment paymentRequest) {

    ParkingTicketAuthorizedPaymentStatus paymentStatus = paymentAuthService.process(paymentRequest, userId, ticketId,
            marketplaceId, storeId, transactionId);

    return new ResponseEntity<>(paymentStatus, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }

  /**
   * Retrieve the status of a given ticket for of a given user
   * @param transactionId
   * @param userId
   * @param ticketId
   * @return ParkingTicketStatus
   */
  @ApiOperation(value = "", nickname = TICKETS_ENDPOINT + "/{ticket_id}")
  @RequestMapping(value = TICKETS_ENDPOINT + "/{ticket_id}", method = RequestMethod.GET, produces = {"application/json"})
  public ResponseEntity<ParkingTicketStatus> getTicketStatus(
      @RequestHeader("X-Supercash-Tid") String transactionId,
      @RequestHeader("X-Supercash-Uid") String userId,
      @RequestHeader("X-Supercash-MarketplaceId") String marketplaceId,
      @RequestHeader("X-Supercash-StoreId") String storeId,
      @PathVariable("ticket_id") String ticketId,
      @RequestParam("sale_id") Optional<Long> saleId) {

    ParkingTicketStatus parkingTicketStatus = statusService.getStatus(userId, ticketId, saleId);

    return new ResponseEntity<>(parkingTicketStatus, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }
}
