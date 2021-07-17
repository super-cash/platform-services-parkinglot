package cash.super_.platform.clients.wps.errors;

import cash.super_.platform.error.supercash.SupercashSimpleException;
import cash.super_.platform.error.supercash.SupercashThirdPartySystemException;
import cash.super_.platform.error.supercash.feign.SupercashAbstractErrorHandler;
import cash.super_.platform.autoconfig.ParkingPlusProperties;
import cash.super_.platform.utils.JsonUtil;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WPSErrorHandler implements SupercashAbstractErrorHandler {

  private static final Logger LOG = LoggerFactory.getLogger(WPSErrorHandler.class);

  @Autowired
  private ParkingPlusProperties properties;

  @Override
  public SupercashSimpleException handle(Response response, String responseBody) {

    SupercashThirdPartySystemException supercashSimpleException = new SupercashThirdPartySystemException();
    String[] contentTypeInfo = response.headers().get("content-type").iterator().next().split(";");

    switch (contentTypeInfo[0]) {
      case MediaType.APPLICATION_JSON_VALUE:
        if (responseBody.contains("mensagem") && responseBody.contains("errorCode")) {
          WPSException wpsException = JsonUtil.toObject(responseBody, WPSException.class);
          supercashSimpleException.SupercashExceptionModel.addField("third_party_message", wpsException.getMessage());
          supercashSimpleException.SupercashExceptionModel.addField("third_party_error_code", wpsException.getErrorCode());
          if (wpsException.getErrorCode() == 1) {
            supercashSimpleException.SupercashExceptionModel.setAdditionalErrorCode(HttpStatus.NOT_FOUND.value());
          }
        }
        // TODO: when any error ocurrs here, the user payment is already processed, we have to deal with any error here
        // including send an SMS or similar (ex. notify the web system) to the responsible of the parking lot and help
        // the user to handle this situation
        //
        // We have to analyse the erroCode here and take decisions:
        // https://demonstracao.parkingplus.com.br/servicos/swagger-ui.html#!/servico-pagamento-ticket-2/pagamentosEfetuadosUsingGET

        break;

      case MediaType.TEXT_HTML_VALUE:
        supercashSimpleException.SupercashExceptionModel.setAdditionalDescription("Third-party returned an HTML " +
                "content.");
        supercashSimpleException.SupercashExceptionModel.addField("third_party_error_content", responseBody);
        break;

    }
    return supercashSimpleException;
  }

  @Override
  public List<String> getRetryableDestinationHosts() {
    return properties.getRetryableDestinationHosts();
  }

}
