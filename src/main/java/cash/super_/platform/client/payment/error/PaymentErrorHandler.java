package cash.super_.platform.client.payment.error;

import cash.super_.platform.autoconfig.ParkingPlusServiceClientProperties;
import cash.super_.platform.adapter.feign.SupercashSimpleException;
import cash.super_.platform.adapter.feign.SupercashAbstractErrorHandler;
import cash.super_.platform.util.JsonUtil;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentErrorHandler implements SupercashAbstractErrorHandler {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentErrorHandler.class);

  @Autowired
  private ParkingPlusServiceClientProperties properties;

  @Override
  public SupercashSimpleException handle(Response response, String responseBody) {

    SupercashSimpleException supercashSimpleException = new SupercashSimpleException();
    String[] contentTypeInfo = response.headers().get("content-type").iterator().next().split(";");

    switch (contentTypeInfo[0]) {
      case MediaType.APPLICATION_JSON_VALUE:
        if (responseBody.contains("SupercashException")) {
          supercashSimpleException = JsonUtil.toObject(responseBody, SupercashSimpleException.class);
        }
        break;

      case MediaType.TEXT_HTML_VALUE:
        supercashSimpleException.SupercashExceptionModel.setAdditionalDescription("Service returned an HTML " +
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
