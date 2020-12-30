package cash.super_.platform.service.parkingplus.ticket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import cash.super_.platform.service.parkingplus.model.ParkingTicketAuthorization;
import cash.super_.platform.service.parkingplus.model.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.service.parkingplus.model.ParkingTicketPaymentsMadeQuery;
import cash.super_.platform.service.parkingplus.model.ParkingTicketPaymentsMadeStatus;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/${cash.super.platform.service.parkingplus.apiVersion}")
public class ParkingPlusPaymentsController extends AbstractController {

  /**
   * The endpoint for the tickets status
   */
  private static final String PAYMENTS_STATUS_ENDPOINT = BASE_ENDPOINT + "/payments/status";
  /**
   * The endpoint for the authorization
   */
  private static final String PAYMENTS_AUTHORIZATION_ENDPOINT = BASE_ENDPOINT + "/payments/authorize";

  @Autowired
  private ParkingPlusTicketPaymentsProxyService paymentsService;

  @Autowired
  private ParkingPlusTicketAuthorizePaymentProxyService paymentAuthService;

  @ApiOperation(value = "", nickname = PAYMENTS_STATUS_ENDPOINT)
  @RequestMapping(value = PAYMENTS_STATUS_ENDPOINT, method = RequestMethod.POST, consumes = {"application/json"},
      produces = {"application/json"})
  public ResponseEntity<ParkingTicketPaymentsMadeStatus> getParkingTicketPaymentsStatus(
      @RequestBody ParkingTicketPaymentsMadeQuery paymentsMadeQuery,
      @RequestHeader("supercash_tid") Optional<String> transactionId,
      @RequestHeader("supercash_uid") Optional<String> userId) throws IOException, InterruptedException {

    ParkingTicketPaymentsMadeStatus parkingTicketStatus = paymentsService.getPaymentsMade(paymentsMadeQuery);

    return new ResponseEntity<>(parkingTicketStatus, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }

  @ApiOperation(value = "", nickname = PAYMENTS_AUTHORIZATION_ENDPOINT)
  @RequestMapping(value = PAYMENTS_AUTHORIZATION_ENDPOINT, method = RequestMethod.POST, consumes = {"application/json"},
      produces = {"application/json"})
  public ResponseEntity<ParkingTicketAuthorizedPaymentStatus> authorizeParkingTicketPayment(
      @RequestBody ParkingTicketAuthorization paymentAuthorization,
      @RequestHeader("supercash_tid") Optional<String> transactionId,
      @RequestHeader("supercash_uid") Optional<String> userId) throws IOException, InterruptedException {

    ParkingTicketAuthorizedPaymentStatus paymentStatus = paymentAuthService.authorizePayment(paymentAuthorization);

    return new ResponseEntity<>(paymentStatus, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }

}
