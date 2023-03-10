package cash.super_.platform.service.parkinglot;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import cash.super_.platform.adapter.http.SupercashRequestContext;
import cash.super_.platform.model.parkinglot.ParkinglotTicketId;
import cash.super_.platform.service.parkinglot.ticket.testing.TestingParkingLotStatusInMemoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import brave.Tracer;
import cash.super_.platform.client.parkingplus.api.ServicoPagamentoTicket2Api;
import cash.super_.platform.autoconfig.ParkingPlusServiceClientProperties;
import cash.super_.platform.util.SecretsUtil;

/**
 * Retrieve the status of tickets, process payments, etc.
 *
 * @author marcellodesales
 *
 */
public abstract class AbstractParkingLotProxyService {

  protected static final Logger LOG = LoggerFactory.getLogger(AbstractParkingLotProxyService.class);

  @Resource(name = "supercashRequestContextInstance")
  protected SupercashRequestContext supercashRequestContext;

  @Autowired
  protected ParkingPlusServiceClientProperties properties;

  @Autowired
  protected Tracer tracer;

  @Autowired
  protected ServicoPagamentoTicket2Api parkingTicketPaymentsApi;

  // Since it's only loaded in certain profiles, autowire is optional
  // https://stackoverflow.com/questions/57656119/how-to-autowire-conditionally-in-spring-boot/57656242#57656242
  @Autowired(required = false)
  protected TestingParkingLotStatusInMemoryRepository testingParkinglotTicketRepository;

  @PostConstruct
  public void postConstruct() {
    // this call if extremely expensive and must be cached
    String userKey = properties.getUserKey();
    Long apiKeyId = properties.getApiKeyId();
    LOG.info("Bootstrapping Parking Plus Service: userKey={} apiKeyId={}", SecretsUtil.obsfucate(userKey), apiKeyId);
  }

  protected String makeWpsUniqueUserId() {
    Long marketplaceId = supercashRequestContext.getMarketplaceId();
    Long storeId = supercashRequestContext.getStoreId();
    Long userId = supercashRequestContext.getUserId();

    String udid = properties.getUdidPrefix() + "-" + marketplaceId + "-" + storeId + "-" + userId;
    LOG.debug("Setting the WPS udid for the user as {}", udid);
    return udid;
  }

  public ParkinglotTicketId makeTicketId(Long ticketNumber) {
    return new ParkinglotTicketId(ticketNumber, supercashRequestContext.getUserId(), supercashRequestContext.getStoreId());
  }

}
