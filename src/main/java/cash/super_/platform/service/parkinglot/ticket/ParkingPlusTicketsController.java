package cash.super_.platform.service.parkinglot.ticket;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import cash.super_.platform.service.parkinglot.model.*;
import cash.super_.platform.service.parkinglot.model.ParkingTicketStatus;
import cash.super_.platform.service.parkinglot.model.SupercashTicketStatus;
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
  @RequestMapping(value = TICKETS_ENDPOINT, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ParkingTicketPaymentsMadeStatus> getParkingTicketPaymentsStatus(
      @RequestHeader("X-Supercash-Tid") String transactionId, 
      @RequestHeader("X-Supercash-Uid") String userId,
      @RequestHeader("X-Supercash-MarketplaceId") String marketplaceId,
      @RequestHeader("X-Supercash-StoreId") String storeId,
      @RequestParam("page_start") Optional<Integer> paginationStart,
      @RequestParam("page_limit") Optional<Integer> paginationLimit) {

    // TODO: define the userId inside the service
    userId =  properties.getUdidPrefix() + "-" + marketplaceId + "-" + storeId + "-" + userId;
    ParkingTicketPaymentsMadeStatus parkingTicketStatus = paymentsService.getPaymentsMade(userId, marketplaceId,
            storeId, paginationStart, paginationLimit);

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
  @RequestMapping(value = TICKETS_ENDPOINT + "/{ticket_number}/pay", method = RequestMethod.POST,
      consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ParkingTicketAuthorizedPaymentStatus> authorizeParkingTicketPayment(
      @RequestHeader("X-Supercash-Tid") String transactionId,
      @RequestHeader("X-Supercash-Uid") String userId,
      @RequestHeader("X-Supercash-MarketplaceId") String marketplaceId,
      @RequestHeader("X-Supercash-StoreId") String storeId,
      @PathVariable("ticket_number") String ticketNumber,
      @RequestBody ParkingTicketPayment paymentRequest) {

    ParkingTicketAuthorizedPaymentStatus paymentStatus = paymentAuthService.process(paymentRequest, userId, ticketNumber,
            marketplaceId, storeId);

    return new ResponseEntity<>(paymentStatus, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }

  /**
   * Retrieve the status of a given ticket for of a given user
   * @param transactionId
   * @param userId
   * @param marketplaceId
   * @param storeId
   * @param ticketNumber
   * @param saleId
   * @return ParkingTicketStatus
   */
  @ApiOperation(value = "", nickname = TICKETS_ENDPOINT + "/{ticket_number}")
  @RequestMapping(value = TICKETS_ENDPOINT + "/{ticket_number}", method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ParkingTicketStatus> getTicketStatus(
      @RequestHeader("X-Supercash-Tid") String transactionId,
      @RequestHeader("X-Supercash-Uid") String userId,
      @RequestHeader("X-Supercash-MarketplaceId") String marketplaceId,
      @RequestHeader("X-Supercash-StoreId") String storeId,
      @PathVariable("ticket_number") String ticketNumber,
      @RequestParam("sale_id") Optional<Long> saleId) {

    // TODO: define the userId inside the service
    userId =  properties.getUdidPrefix() + "-" + marketplaceId + "-" + storeId + "-" + userId;
    ParkingTicketStatus parkingTicketStatus = statusService.getStatus(userId, ticketNumber, saleId);

    return new ResponseEntity<>(parkingTicketStatus, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }
//
//  @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
//  public ResponseEntity handle() { return new ResponseEntity(HttpStatus.OK); }

    /**
     * Retrieve the status of a given ticket of a given user in Supercash database
     * @param transactionId
     * @param userId
     * @param marketplaceId
     * @param storeId
     * @param ticketNumber
     * @return getTicketLocalStatus
     */
  @ApiOperation(value = "", nickname = TICKETS_ENDPOINT + "/local/{ticket_number}")
  @RequestMapping(value = TICKETS_ENDPOINT + "/local/{ticket_number}", method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE})
  public @ResponseBody Map<String, Integer> getTicketLocalStatus(
          @RequestHeader("X-Supercash-Tid") String transactionId,
          @RequestHeader("X-Supercash-Uid") String userId,
          @RequestHeader("X-Supercash-MarketplaceId") String marketplaceId,
          @RequestHeader("X-Supercash-StoreId") String storeId,
          @PathVariable("ticket_number") String ticketNumber) {

    // TODO: define the userId inside the service
    userId =  properties.getUdidPrefix() + "-" + marketplaceId + "-" + storeId + "-" + userId;
//    ParkingTicketStatus parkingTicketStatus = statusService.getLocalStatus(userId, ticketNumber);
    return new HashMap<>() {{
      put("status", SupercashTicketStatus.PAID.ordinal());
    }};
  }

}
