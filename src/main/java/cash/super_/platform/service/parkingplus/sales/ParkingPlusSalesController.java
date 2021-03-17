package cash.super_.platform.service.parkingplus.sales;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import cash.super_.platform.client.parkingplus.model.Promocao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import cash.super_.platform.service.parkingplus.AbstractController;
import cash.super_.platform.service.parkingplus.model.ParkingGarageSales;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/${cash.super.platform.service.parkingplus.apiVersion}")
public class ParkingPlusSalesController extends AbstractController {

  private static final Logger LOG = LoggerFactory.getLogger(ParkingPlusSalesController.class);

  /**
   * The endpoint for the authorization
   */
  private static final String TICKETS_SALES_ENDPOINT = BASE_ENDPOINT + "/sales";

  @Autowired
  private ParkingPlusParkingSalesCachedProxyService parkingSalesService;

  @ApiOperation(value = "", nickname = TICKETS_SALES_ENDPOINT)
  @RequestMapping(value = TICKETS_SALES_ENDPOINT, method = RequestMethod.GET, produces = {"application/json"})
  public ResponseEntity<ParkingGarageSales> retrieveParkingSales(
      @RequestHeader("X-Supercash-Tid") String transactionId,
      @RequestHeader("X-Supercash-Uid") String headerUserId,
      @PathVariable("supercash_uid") String userId) throws IOException, InterruptedException {

    isRequestValid(headerUserId, userId);

    ParkingGarageSales currentParkingGarageSales = parkingSalesService.fetchCurrentGarageSales();

    return new ResponseEntity<>(currentParkingGarageSales, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }

  @ApiOperation(value = "", nickname = TICKETS_SALES_ENDPOINT + "/{sale_id}")
  @RequestMapping(value = TICKETS_SALES_ENDPOINT + "/{sale_id}", method = RequestMethod.GET, produces = {"application/json"})
  public ResponseEntity<Promocao> retrieveParkingSale(
          @RequestHeader("X-Supercash-Tid") String transactionId,
          @RequestHeader("X-Supercash-Uid") String headerUserId,
          @PathVariable("supercash_uid") String userId,
          @PathVariable("sale_id") Long saleId,
          @RequestParam("validate") Optional<Boolean> validate) throws IOException, InterruptedException {

    isRequestValid(headerUserId, userId);

    Promocao sale = parkingSalesService.getSale(saleId);

    if (validate.isEmpty() || validate.get()) {
      parkingSalesService.isSaleValid(sale, true);
    }

    return new ResponseEntity<>(sale, makeDefaultHttpHeaders(new HashMap<>()), HttpStatus.OK);
  }
}
