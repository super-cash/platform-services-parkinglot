package cash.super_.platform.error.supercash.feign;

import cash.super_.platform.error.supercash.SupercashSimpleException;
import cash.super_.platform.error.supercash.SupercashUnknownHostException;
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
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

// For more info: https://medium.com/swlh/how-to-customize-feigns-retry-mechanism-b472202be331

public class SupercashErrorDecoder implements ErrorDecoder {

  protected static final Logger LOG = LoggerFactory.getLogger(SupercashErrorDecoder.class);

  private final ErrorDecoder defaultErrorDecoder = new Default();

  private SupercashAbstractErrorHandler errorHandler;

  public SupercashErrorDecoder(SupercashAbstractErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  @Override
  public Exception decode(String methodKey, Response response) {
    Exception exception = defaultErrorDecoder.decode(methodKey, response);

    if (exception instanceof RetryableException) {
      if (exception.getCause() instanceof UnknownHostException) {
        return new SupercashUnknownHostException("Host " + exception.getCause().getMessage() + " unknown.");
      }
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
      LOG.error("[Class: {}] Response body of the error: {}", this.getClass().getSimpleName(), responseBody);

      Request request = response.request();
      URL url = null;
      try {
        url = new URL(request.url());
      } catch (MalformedURLException e) { /* Since it is a valid request, this exception will never happens */ }

      SupercashSimpleException supercashSimpleException = null;
      if (errorHandler != null) {
        supercashSimpleException = errorHandler.handle(response, responseBody);
      }
//        } else if (responseBody.contains("errors") && responseBody.contains("parameter_name")) {
//          PagarmeException pagarmeException = JsonUtil.toObject(responseBody, PagarmeException.class);
//          supercashSimpleException = new SupercashThirdPartySystemException();
//          supercashSimpleException.SupercashExceptionModel.addField("third_party_errors", pagarmeException.getErrors());
//          supercashSimpleException.SupercashExceptionModel.addField("third_party_url", pagarmeException.getUrl());
//          supercashSimpleException.SupercashExceptionModel.addField("third_party_method", pagarmeException.getMethod());
//        } else {
//          // TODO: implement pagseguro errors handing
//        } else {
//          supercashSimpleException = JsonUtil.toObject(responseBody, SupercashSimpleException.class);
//        }
//      } else {
//        // TODO: create default exception to handle other types of content-type
//        LOG.error("Occurred a third-party error, but it is not a JSON content type.");
//      }

      // TODO: implement specific pagseguro errors here based on this template
//      {
//        "error_messages" : [ {
//        "code" : "40001",
//                "description" : "required_parameter",
//                "parameter_name" : "payment_method.card.number"
//      } ]
//      }
      // TODO: Send SMS to the admin

      if (HttpStatus.valueOf(response.status()).is5xxServerError() ||
              HttpStatus.valueOf(response.status()) == HttpStatus.NOT_ACCEPTABLE) {
        List<String> retryableDestinationHosts = errorHandler.getRetryableDestinationHosts();
        if (retryableDestinationHosts.contains(url.getHost()) || retryableDestinationHosts.contains(url.getPort()) ||
                retryableDestinationHosts.contains(url.getHost() + ":" + url.getPort())) {
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

  public SupercashAbstractErrorHandler getErrorHandler() {
    return errorHandler;
  }

  public void setErrorHandler(SupercashAbstractErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }
}