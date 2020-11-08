package cash.super_.platform.service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cash.super_.platform.client.parkingplus.api.ServicoPagamentoTicket2Api;
import cash.super_.platform.client.parkingplus.invoker.ApiClient;
import cash.super_.platform.service.parkingplus.ParkingPlusProperties;

/**
 * Bootstraps the feign clients as described in the client documentation. Haven't found any documentation
 * on how to setup the client
 *
 * https://gitlab.com/supercash/clients/parking-plus-client-feign/-/blob/master/src/main/java/cash/super_/platform/client/parkingplus/invoker/ApiClient.java#L136-147
 * @author marcellodesales
 *
 */
@Configuration
public class ParkingPlusFeignClientConfiguration {

  @Autowired
  private ParkingPlusProperties properties;

  @Bean
  public ServicoPagamentoTicket2Api ticketApi() {
    ApiClient client = new ApiClient();
    // https://stackoverflow.com/questions/42751269/feign-logging-not-working/59651045#59651045
    client.getFeignBuilder().logLevel(properties.getClientLogLevel());
    client.setBasePath(properties.getHost());
    // Generated from swagger: https://demonstracao.parkingplus.com.br/servicos
    return client.buildClient(ServicoPagamentoTicket2Api.class);
  }

}