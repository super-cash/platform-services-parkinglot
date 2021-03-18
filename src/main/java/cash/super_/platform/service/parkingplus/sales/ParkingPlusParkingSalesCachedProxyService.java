package cash.super_.platform.service.parkingplus.sales;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import cash.super_.platform.error.ParkingPlusInvalidSalesException;
import cash.super_.platform.error.ParkingPlusSalesNotFoundException;
import cash.super_.platform.error.supercash.SupercashInvalidValueException;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.cache.CacheBuilder;
import brave.Span;
import brave.Tracer;
import brave.Tracer.SpanInScope;
import cash.super_.platform.client.parkingplus.model.Promocao;
import cash.super_.platform.service.parkingplus.autoconfig.ParkingPlusProperties;
import cash.super_.platform.service.parkingplus.model.ParkingGarageSales;
import cash.super_.platform.utils.JsonUtil;
import cash.super_.platform.utils.SecretsUtil;

/**
 * The Service is also a cache loader, as a separate class would not be different. Too little to be
 * added in isolation. Based on https://www.baeldung.com/guava-cache.
 *
 * @author marcellodesales
 *
 */
@Service
public class ParkingPlusParkingSalesCachedProxyService
    extends AbstractCacheableParkingLotProxyService<Long, ParkingGarageSales> {

  private static final Logger LOG = LoggerFactory.getLogger(ParkingPlusParkingSalesCachedProxyService.class);

  @Autowired
  private ParkingPlusProperties properties;

  @Autowired
  private Tracer tracer;

  /**
   * @return The number of supercash sales for the current parkinglot configured
   */
  public long getNumberOfSales() {
    ParkingGarageSales supercashSales = cache.getUnchecked(properties.getParkingLotId());
    return supercashSales.getCurrent().size();
  }

  @PostConstruct
  public void postConstruct() {
    // this call if extremely expensive and must be cached
    String userKey = properties.getUserKey();
    Long apiKeyId = properties.getApiKeyId();
    LOG.info("Bootstrapping Cacheable Parking Plus Service: userKey={} apiKeyId={}", SecretsUtil.obsfucate(userKey), apiKeyId);

    // If we need to cache locations that are frequently searched, we can add them here
    LOG.info("Bootstrapping parking garage sales Cache; eviction time of {} {}", properties.getSalesCacheDuration(),
        properties.getSalesCacheTimeUnit());

    this.cache = CacheBuilder.newBuilder()
        .expireAfterAccess(properties.getSalesCacheDuration(), properties.getSalesCacheTimeUnit()).build(this);
    LOG.info("Initialized the Results Cache");
  }

  public boolean isSaleValid(Promocao sale, boolean throwException) {
    String message = "";
    if (sale == null) {
      if (throwException) {
        throw new ParkingPlusSalesNotFoundException(message);
      }
      return false;
    }

    String[] weekDayNames = new String[] {
            "",
            "SEGUNDA",
            "TERCA",
            "QUARTA",
            "QUINTA",
            "SEXTA",
            "SABADO",
            "DOMINGO"
    };

    int weekDayIndex = LocalDateTime.now().getDayOfWeek().getValue();
    int saleDaysSize = sale.getDiasSemana().size();
    int i = 0;
    for (; i < saleDaysSize; i++) {
      if (sale.getDiasSemana().get(i).ordinal() == weekDayIndex) break;
    }

    DateTime todayDate = DateTime.now();
    LocalTime todayTime = LocalTime.now();
    LocalTime saleStartTime = sale.getHorarioInicio().toLocalTime();
    LocalTime saleEndTime = sale.getHorarioFim().toLocalTime();

    if (i == saleDaysSize || todayDate.isAfter(sale.getValidade()) || (saleStartTime.isBefore(saleEndTime) &&
            (todayTime.isBefore(saleStartTime) || todayTime.isAfter(saleEndTime)))) {
      message = "Sale with ID " + sale.getSystemId() + " is not available for today and/or at this time, or even it " +
              "has expired";
      LOG.error(message);
      if (throwException) {
        throw new ParkingPlusInvalidSalesException(message);
      }
      return false;
    }
    return true;
  }

  /**
   * @return A sale of a given id.
   */
  public Promocao getSale(Long saleId, boolean throwException, Optional<Boolean> validate) {
    if (saleId == null) return null;

    Promocao sale = null;
    boolean found = false;
    for (Promocao _sale : cache.getUnchecked(properties.getParkingLotId()).getCurrent()) {
      if (_sale.getSystemId() == saleId) {
        sale = _sale;
        found = true;
        break;
      }
    }

    if (found) {
      if (validate.isEmpty() || validate.get()) {
        isSaleValid(sale, throwException);
      }
      return sale;
    }
    return null;
  }

  public ParkingGarageSales fetchCurrentGarageSales() {
    LOG.debug("Got garage sales list for garage ID={}", this.properties.getParkingLotId());

    if (this.properties.getParkingLotId() <= 0) {
      throw new SupercashInvalidValueException("The Parking garage must be a valid number");
    }

    ParkingGarageSales distanceResult = cache.getUnchecked(this.properties.getParkingLotId());
    return distanceResult;
  }

  /**
   * Loads a given addresses key when it is NOT in cache.
   */
  @Override
  public ParkingGarageSales load(Long parkingGarageId) {
    LOG.info("Garage ID not in cache: {}", parkingGarageId);

    // The calculation from Google
    List<Promocao> supercashParkingSales = new ArrayList<>();

    Span newSpan = tracer.nextSpan().name("REST https://parkingplus.com.br/2/promocoes").start();
    try (SpanInScope ws = tracer.withSpanInScope(newSpan.start())) {

      LOG.info("Requesting the parking sales");

      // Build the query parameter, with the API Key being the only secret
      String apiKey = SecretsUtil.makeApiKey(properties.getUserKey());

      long apiKeyId = properties.getApiKeyId();
      String guidGaragem = null;
      List<String> tiposPromocao = null;
      String numeroTicket = null;
      String token = null;

      try {
        // These are all parking sales for this garage, no matter who the third-party integrator is.
        for (Promocao sale : this.parkingTicketPaymentsApi.getPromocoesUsingGET(apiKey, apiKeyId, parkingGarageId,
            guidGaragem, tiposPromocao, numeroTicket, token)) {

          // Just add the ones for Supercash
          if (sale.getNome().startsWith("SUPERCASH")) {
            supercashParkingSales.add(sale);
          }
        }

      } catch (Exception e) {
        LOG.error("Couldn't fetch supercash sales. Please, contact the WSP Representatives!");
      }

//      if (supercashParkingSales.size() == 0) {
//        String message = "Can't fetch the supercash sales. Please, contact the WSP Representative!";
//        LOG.error(message);
//        throw new IllegalStateException(message);
//      }

      // Just the names of the promos for tracing
      String salesNames = supercashParkingSales.stream()
          .map(sale -> sale.getTitulo())
          .sorted()
          .collect(Collectors.joining(","));

      newSpan.tag("sales", String.valueOf(salesNames));

    } finally {
      newSpan.finish();
    }

    ParkingGarageSales sales = new ParkingGarageSales(supercashParkingSales);
    try {
      LOG.info("Built the parking sales cache => " + JsonUtil.toJson(sales));

    } catch (JsonProcessingException error) {
      LOG.error("Can't transform the sales into json for logging: ", error);
    }
    return sales;
  }

}
