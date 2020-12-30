package cash.super_.platform.service.parkingplus.sales;

import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import brave.Tracer;
import cash.super_.platform.client.parkingplus.api.ServicoPagamentoTicket2Api;
import cash.super_.platform.service.parkingplus.autoconfig.ParkingPlusProperties;

/**
 * Retrieve the status of tickets, process payments, etc.
 *
 * @author marcellodesales
 *
 */
public abstract class AbstractCacheableParkingLotProxyService<K, V> extends CacheLoader<K, V> {

  @Autowired
  protected ParkingPlusProperties properties;

  @Autowired
  protected Tracer tracer;

//  @Autowired 
//  protected Tracing tracing;

  @Autowired
  protected ServicoPagamentoTicket2Api parkingTicketPaymentsApi;

  /**
   * The Cache of results based on the input https://www.baeldung.com/guava-cache. It has an eviction
   * policy of x minutes for older results to be removed.
   */
  protected LoadingCache<K, V> cache;

  /**
   * @return The size of the cache based on the number of keys
   */
  public long getCacheSize() {
    return this.cache.size();
  }

}
