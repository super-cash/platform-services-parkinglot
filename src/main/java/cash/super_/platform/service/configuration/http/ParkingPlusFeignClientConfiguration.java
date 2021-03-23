package cash.super_.platform.service.configuration.http;

import cash.super_.platform.error.supercash.feign.SupercashErrorDecoder;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cash.super_.platform.client.parkingplus.api.ServicoPagamentoTicket2Api;
import cash.super_.platform.client.parkingplus.invoker.ApiClient;
import cash.super_.platform.service.parkingplus.autoconfig.ParkingPlusProperties;

import java.util.concurrent.TimeUnit;

/**
 * Bootstraps the feign clients as described in the client documentation. Haven't found any documentation
 * on how to setup the client
 *
 * https://gitlab.com/supercash/clients/parking-plus-client-feign/-/blob/master/src/main/java/cash/super_/platform/client/parkingplus/invoker/ApiClient.java#L136-147
 * 
 * https://www.javacodegeeks.com/2018/06/provide-client-libraries-apis.html
 * https://arnoldgalovics.com/generating-feign-clients-with-swagger-codegen-and-gradle/
 * 
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
    client.setBasePath(properties.getUrl());
    // https://stackoverflow.com/questions/42751269/feign-logging-not-working/59651045#59651045
    client.getFeignBuilder()
            .logLevel(properties.getClientLogLevel())
            .errorDecoder(new SupercashErrorDecoder(properties.getRetryableDestinationHosts()))
            .retryer(new Retryer.Default(TimeUnit.SECONDS.toMillis(properties.getRetryInterval()),
                    TimeUnit.SECONDS.toMillis(properties.getRetryMaxPeriod()), properties.getRetryMaxAttempt()));

    // ADd the tracing client to call other microservices
    // https://github.com/yandok/DistributedTracing-Example/blob/master/DistributedTracing-AppB/src/main/java/yan/dok/OpenTracingAppB/GreetingController.java#L27
    //TODO: https://medium.com/@klaus.dobbler/introducing-distributed-tracing-to-a-docker-swarm-landscape-f92c033e36db
    //client.getFeignBuilder().client(new TracingClient)

    // Generated from swagger: https://demonstracao.parkingplus.com.br/servicos
    return client.buildClient(ServicoPagamentoTicket2Api.class);
  }

}