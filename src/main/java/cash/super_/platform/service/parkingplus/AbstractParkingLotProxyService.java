package cash.super_.platform.service.parkingplus;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import brave.Tracer;
import cash.super_.platform.client.parkingplus.api.ServicoPagamentoTicket2Api;
import cash.super_.platform.service.parkingplus.util.SecretsUtil;

/**
 * Retrieve the status of tickets, process payments, etc.
 *
 * @author marcellodesales
 *
 */
public abstract class AbstractParkingLotProxyService {

  protected static final Logger LOG = LoggerFactory.getLogger(AbstractParkingLotProxyService.class);

  @Autowired
  protected ParkingPlusProperties properties;

  @Autowired
  protected Tracer tracer;

//  @Autowired 
//  protected Tracing tracing;

  @Autowired
  protected ServicoPagamentoTicket2Api parkingTicketPaymentsApi;

  @PostConstruct
  public void postConstruct() {
    // this call if extremely expensive and must be cached
    String userKey = properties.getUserKey();
    Long apiKeyId = properties.getApiKeyId();
    LOG.info("Bootstrapping Parking Plus Service: userKey={} apiKeyId={}", SecretsUtil.obsfucate(userKey), apiKeyId);
  }

}
