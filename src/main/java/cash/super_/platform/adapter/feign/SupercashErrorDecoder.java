package cash.super_.platform.adapter.feign;

import cash.super_.platform.error.parkinglot.SupercashSimpleException;
import cash.super_.platform.error.parkinglot.SupercashUnknownHostException;
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

      if (supercashSimpleException == null) {
        return exception;
      }

      // TODO: Send SMS to the admin

      LOG.debug("Checking if error is 5xx or {}... ", HttpStatus.NOT_ACCEPTABLE);
      if (HttpStatus.valueOf(response.status()).is5xxServerError() ||
              HttpStatus.valueOf(response.status()) == HttpStatus.NOT_ACCEPTABLE) {
        List<String> retryableDestinationHosts = errorHandler.getRetryableDestinationHosts();
        LOG.debug("List of retryable destinations: {}... ", retryableDestinationHosts);
        if (retryableDestinationHosts.contains(url.getHost()) || retryableDestinationHosts.contains(url.getPort()) ||
                retryableDestinationHosts.contains(url.getHost() + ":" + url.getPort())) {
          LOG.error("Retrying send request to {} due to response with HTTP Status {}. Request:\n{}", request.url(),
                  HttpStatus.valueOf(response.status()), response.request());
          return new SupercashRetryableException(response.status(), "Retry due to http status " +
                  HttpStatus.valueOf(response.status()), request.httpMethod(), supercashSimpleException, null, request);
        }
      } else {
        LOG.debug("... No!");
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