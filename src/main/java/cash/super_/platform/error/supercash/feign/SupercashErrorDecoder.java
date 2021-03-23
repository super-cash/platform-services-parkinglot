package cash.super_.platform.error.supercash.feign;

import cash.super_.platform.error.model.SupercashExceptionModel;
import cash.super_.platform.error.supercash.SupercashSimpleException;
import cash.super_.platform.error.supercash.SupercashThirdPartySystemSimpleException;
import cash.super_.platform.error.thirdparty.PagarmeException;
import cash.super_.platform.error.thirdparty.WPSException;
import cash.super_.platform.utils.JsonUtil;
import feign.FeignException;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// For more info: https://medium.com/swlh/how-to-customize-feigns-retry-mechanism-b472202be331

public class SupercashErrorDecoder implements ErrorDecoder {

  protected static final Logger LOG = LoggerFactory.getLogger(SupercashErrorDecoder.class);

  private List<String> retryableDestinationHosts = new ArrayList<>();

  private final ErrorDecoder defaultErrorDecoder = new Default();

  public SupercashErrorDecoder(List<String> retryableDestinationHosts) {
    this.retryableDestinationHosts = retryableDestinationHosts;
  }

  @Override
  public Exception decode(String methodKey, Response response) {
    Exception exception = defaultErrorDecoder.decode(methodKey, response);
    if (exception instanceof RetryableException) {
      return exception;
    }

    FeignException feignException = null;
    boolean hasBody = false;
    ByteBuffer bodyBuffer = null;
    if (exception instanceof FeignException) {
      feignException = (FeignException) exception;

      if (feignException.responseBody().isPresent()) {
        bodyBuffer = feignException.responseBody().get();
        hasBody = bodyBuffer.hasArray();
      }
    }

    String responseBody = "";
    if (hasBody) {
      responseBody = new String(bodyBuffer.array(), StandardCharsets.UTF_8);
      SupercashSimpleException supercashSimpleException = null;
      SupercashExceptionModel supercashExceptionModel = null;
      if (responseBody.contains("mensagem") && responseBody.contains("errorCode")) {
        WPSException wpsException = JsonUtil.toObject(responseBody, WPSException.class);
        supercashSimpleException = new SupercashThirdPartySystemSimpleException();
        supercashSimpleException.SupercashExceptionModel.addField("third_party_message", wpsException.getMessage());
        supercashSimpleException.SupercashExceptionModel.addField("third_party_error_code", wpsException.getErrorCode());
      } else if (responseBody.contains("errors") && responseBody.contains("parameter_name")) {
        PagarmeException pagarmeException = JsonUtil.toObject(responseBody, PagarmeException.class);
        supercashSimpleException = new SupercashThirdPartySystemSimpleException();
        supercashSimpleException.SupercashExceptionModel.addField("third_party_errors", pagarmeException.getErrors());
        supercashSimpleException.SupercashExceptionModel.addField("third_party_url", pagarmeException.getUrl());
        supercashSimpleException.SupercashExceptionModel.addField("third_party_method", pagarmeException.getMethod());
      } else {
        supercashSimpleException = JsonUtil.toObject(responseBody, SupercashSimpleException.class);
      }

      if (HttpStatus.valueOf(response.status()).is5xxServerError()) {
        Request request = response.request();
        URL url = null;
        try {
          url = new URL(request.url());
        } catch (MalformedURLException e) {
          // Since it is a valid request, this exception will never happens }
        }
        if (retryableDestinationHosts.contains(url.getHost()) || retryableDestinationHosts.contains(url.getPort()) ||
                retryableDestinationHosts.contains(url.getHost() + ":" + url.getPort())) {
          // TODO: Send SMS to the admin
          if (supercashSimpleException == null) {
            supercashSimpleException = new SupercashSimpleException();
          }
          LOG.error("Retrying send request to {} due to response with HTTP Status {}. Request:\n{}", request.url(),
                  HttpStatus.valueOf(response.status()), response.request());
          return new SupercashRetryableException(response.status(), "Retry due to http status " +
                  HttpStatus.valueOf(response.status()), request.httpMethod(), supercashSimpleException, null, request);
        }
      }

      if (supercashSimpleException != null) {
        return supercashSimpleException;
      }
    }

    return defaultErrorDecoder.decode(methodKey, response);
  }
}
