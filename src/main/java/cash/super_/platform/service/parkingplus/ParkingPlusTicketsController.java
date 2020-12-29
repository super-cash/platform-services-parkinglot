package cash.super_.platform.service.parkingplus;

import java.io.IOException;
import java.util.HashMap;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import cash.super_.platform.service.parkingplus.model.ParkingTicket;
import cash.super_.platform.service.parkingplus.model.ParkingTicketStatus;
import io.swagger.annotations.ApiOperation;

@Controller
@Validated
@RequestMapping("/${cash.super.platform.service.parkingplus.apiVersion}")
public class ParkingPlusTicketsController extends AbstractController {

  /**
   * The endpoint for the tickets status
   */
  private static final String TICKETS_STATUS_ENDPOINT = BASE_ENDPOINT + "/tickets/status";

  @Autowired
  private ParkingPlusTicketStatusProxyService statusService;

  // The name in swagger metadata is coming as "operationId":"distancematrixUsingPOST"
  // https://stackoverflow.com/questions/38821763/how-to-customize-the-value-of-operationid-generated-in-api-spec-with-swagger/59044919#59044919
  @ApiOperation(value = "", nickname = TICKETS_STATUS_ENDPOINT)
  @RequestMapping(value = TICKETS_STATUS_ENDPOINT, method = RequestMethod.POST, consumes = {"application/json"},
      produces = {"application/json"})
  public ResponseEntity<ParkingTicketStatus> getTicketStatus(@Valid @RequestBody ParkingTicket parkingTicket,
      @RequestHeader("supercash_tid") String transactionId,
      @RequestHeader("supercash_uid") String userId) throws IOException, InterruptedException {

    ParkingTicketStatus parkingTicketStatus = statusService.getStatus(userId, parkingTicket);

    return new ResponseEntity<>(parkingTicketStatus, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }
}
