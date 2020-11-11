package cash.super_.platform.service.parkingplus;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import cash.super_.platform.service.parkingplus.model.ParkingGarageSales;
import cash.super_.platform.service.parkingplus.model.ParkingTicket;
import cash.super_.platform.service.parkingplus.model.ParkingTicketAuthorization;
import cash.super_.platform.service.parkingplus.model.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.service.parkingplus.model.ParkingTicketPaymentsMadeQuery;
import cash.super_.platform.service.parkingplus.model.ParkingTicketPaymentsMadeStatus;
import cash.super_.platform.service.parkingplus.model.ParkingTicketStatus;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/${cash.super.platform.service.parkingplus.apiVersion}")
public class ParkingPlusProxyController extends AbstractController {

  /**
   * Where the call will come through
   */
  public static final String BASE_ENDPOINT = "/parking_lots";
  /**
   * The endpoint for the tickets status
   */
  private static final String TICKETS_STATUS_ENDPOINT = BASE_ENDPOINT + "/tickets/status";
  /**
   * The endpoint for the authorization
   */
  private static final String TICKETS_SALES_ENDPOINT = BASE_ENDPOINT + "/sales";
  /**
   * The endpoint for the tickets status
   */
  private static final String PAYMENTS_STATUS_ENDPOINT = BASE_ENDPOINT + "/payments/status";
  /**
   * The endpoint for the authorization
   */
  private static final String PAYMENTS_AUTHORIZATION_ENDPOINT = BASE_ENDPOINT + "/payments/authorize";

  @Autowired
  private ParkingPlusTicketStatusProxyService statusService;

  @Autowired
  private ParkingPlusTicketPaymentsProxyService paymentsService;

  @Autowired
  private ParkingPlusTicketAuthorizePaymentProxyService paymentAuthService;

  @Autowired
  private ParkingPlusParkingSalesCachedProxyService parkingSalesService;

  // The name in swagger metadata is coming as "operationId":"distancematrixUsingPOST"
  // https://stackoverflow.com/questions/38821763/how-to-customize-the-value-of-operationid-generated-in-api-spec-with-swagger/59044919#59044919
  @ApiOperation(value = "", nickname = TICKETS_STATUS_ENDPOINT)
  @RequestMapping(value = TICKETS_STATUS_ENDPOINT, method = RequestMethod.POST, consumes = {"application/json"},
      produces = {"application/json"})
  public ResponseEntity<ParkingTicketStatus> getTicketStatus(@RequestBody ParkingTicket parkingTicket,
      @RequestHeader("supercash_tid") Optional<String> transactionId,
      @RequestHeader("supercash_cid") Optional<String> clientId) throws IOException, InterruptedException {

    ParkingTicketStatus parkingTicketStatus = statusService.getStatus(parkingTicket);

    Map<String, String> responseHeaders = setOptionalResponseHeaders(transactionId, clientId);

    return new ResponseEntity<>(parkingTicketStatus, makeDefaultHttpHeaders(responseHeaders), HttpStatus.OK);
  }

  @ApiOperation(value = "", nickname = PAYMENTS_STATUS_ENDPOINT)
  @RequestMapping(value = PAYMENTS_STATUS_ENDPOINT, method = RequestMethod.POST, consumes = {"application/json"},
      produces = {"application/json"})
  public ResponseEntity<ParkingTicketPaymentsMadeStatus> getParkingTicketPaymentsStatus(
      @RequestBody ParkingTicketPaymentsMadeQuery paymentsMadeQuery,
      @RequestHeader("supercash_tid") Optional<String> transactionId,
      @RequestHeader("supercash_cid") Optional<String> clientId) throws IOException, InterruptedException {

    ParkingTicketPaymentsMadeStatus parkingTicketStatus = paymentsService.getPaymentsMade(paymentsMadeQuery);

    Map<String, String> responseHeaders = setOptionalResponseHeaders(transactionId, clientId);

    return new ResponseEntity<>(parkingTicketStatus, makeDefaultHttpHeaders(responseHeaders), HttpStatus.OK);
  }

  @ApiOperation(value = "", nickname = PAYMENTS_AUTHORIZATION_ENDPOINT)
  @RequestMapping(value = PAYMENTS_AUTHORIZATION_ENDPOINT, method = RequestMethod.POST, consumes = {"application/json"},
      produces = {"application/json"})
  public ResponseEntity<ParkingTicketAuthorizedPaymentStatus> authorizeParkingTicketPayment(
      @RequestBody ParkingTicketAuthorization paymentAuthorization,
      @RequestHeader("supercash_tid") Optional<String> transactionId,
      @RequestHeader("supercash_cid") Optional<String> clientId) throws IOException, InterruptedException {

    ParkingTicketAuthorizedPaymentStatus paymentStatus = paymentAuthService.authorizePayment(paymentAuthorization);

    Map<String, String> responseHeaders = setOptionalResponseHeaders(transactionId, clientId);

    return new ResponseEntity<>(paymentStatus, makeDefaultHttpHeaders(responseHeaders), HttpStatus.OK);
  }

  @ApiOperation(value = "", nickname = TICKETS_SALES_ENDPOINT)
  @RequestMapping(value = TICKETS_SALES_ENDPOINT, method = RequestMethod.GET, produces = {"application/json"})
  public ResponseEntity<ParkingGarageSales> retrieveParkingSales(
      @RequestHeader("supercash_tid") Optional<String> transactionId,
      @RequestHeader("supercash_cid") Optional<String> clientId) throws IOException, InterruptedException {

    ParkingGarageSales currentParkingGarageSales = parkingSalesService.fetchCurrentGarageSales();

    Map<String, String> responseHeaders = setOptionalResponseHeaders(transactionId, clientId);

    return new ResponseEntity<>(currentParkingGarageSales, makeDefaultHttpHeaders(responseHeaders), HttpStatus.OK);
  }
}
