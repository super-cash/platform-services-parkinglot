package cash.super_.platform.service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cash.super_.platform.client.parkingplus.api.ServicoPagamentoTicket2Api;
import cash.super_.platform.client.parkingplus.invoker.ApiClient;
import cash.super_.platform.service.parkingplus.ParkingPlusProperties;

@Configuration
public class ParkingPlusFeignClientConfiguration {

  @Autowired
  private ParkingPlusProperties properties;

  @Bean
  public ServicoPagamentoTicket2Api ticketApi() {
    ApiClient client = new ApiClient();
    // Generated from swagger: https://demonstracao.parkingplus.com.br/servicos
    client.setBasePath(properties.getHost());
    return client.buildClient(ServicoPagamentoTicket2Api.class);
  }
}