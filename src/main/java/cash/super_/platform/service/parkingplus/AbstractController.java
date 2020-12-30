package cash.super_.platform.service.parkingplus;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import cash.super_.platform.service.parkingplus.autoconfig.ParkingPlusProperties;
import feign.FeignException;

/**
 * Abstract controller error that can be reused by other services.
 * Based on https://howtodoinjava.com/spring-boot2/spring-rest-request-validation/
 * @author marcellodesales
 *
 */
public abstract class AbstractController extends ResponseEntityExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractController.class);

  /**
   * Where the call will come through
   */
  public static final String BASE_ENDPOINT = "/parking_lots/1/users/{supercash_uid}";

  @Autowired
  protected ParkingPlusProperties properties;

  /**
   * @return The default headers for all Controller Calls
   */
  protected HttpHeaders makeDefaultHttpHeaders(Map<String, String> additionalHeaders) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("api-version", properties.getApiVersion());

    // Merge additional headers to the response
    for (String key : additionalHeaders.keySet()) {
      headers.add(key, additionalHeaders.get(key));
    }
    return headers;
  }

  /**
   * <p>
   * More about global and controller exceptions in the references
   * </p>
   * <ul>
   * <li><a href=
   * "http://www.ekiras.com/2016/02/how-to-do-exception-handling-in-springboot-rest-application.html">Feb
   * 2016: How to do exception handling in Spring Boot ReST
   * applications</a></li>http://www.ekiras.com/2016/02/how-to-do-exception-handling-in-springboot-rest-application.html
   * <li><a href="https://www.jayway.com/2014/10/19/spring-boot-error-responses/">Oct 2014: Spring
   * Boot error responses</a></li>
   * https://www.baeldung.com/exception-handling-for-rest-with-spring
   * </ul>
   *
   * @param error is the exception that was thrown
   * @param response is the response object.
   * @throws IOException while sending the error back to the client.
   */
  @ExceptionHandler(value = {Exception.class, MissingRequestHeaderException.class})
  public final ResponseEntity<Object> handleAllExceptions(Exception error, WebRequest request) {
    LOG.trace("Error handling the request: ", error);
    if (error instanceof FeignException.NotFound) {
      FeignException.NotFound feignError = (FeignException.NotFound)error;
      String message = feignError.getMessage();
      if (message.contains("errorCode") && message.contains(":31")) {
        return makeErrorResponse(error, message, HttpStatus.BAD_REQUEST);
      }
    }
    if (error instanceof FeignException.Forbidden) {
      FeignException.Forbidden feignError = (FeignException.Forbidden)error;
      int status = feignError.status();
      String message = feignError.getMessage();
      if (status == 403 && (message.contains("não encontrado") || message.contains("não existe"))) {
        return makeErrorResponse(error, message, HttpStatus.NOT_FOUND);
      }
    }

    return makeErrorResponse(error, error.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * This is to avoid ambiguity trying to handle new methods
   */
  // https://stackoverflow.com/questions/51991992/getting-ambiguous-exceptionhandler-method-mapped-for-methodargumentnotvalidexce/51993609#51993609
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                HttpHeaders headers, HttpStatus status, WebRequest request) {
    List<String> validationList = ex.getBindingResult().getFieldErrors()
        .stream()
        .map(fieldError -> fieldError.toString())
        .collect(Collectors.toList());
    return makeErrorResponse(ex, validationList.toString(), HttpStatus.BAD_REQUEST);
  }

  private ResponseEntity<Object> makeErrorResponse(Exception errorCause, String message, HttpStatus returnStatusCode) {
    LOG.trace("Error handling the request: ", errorCause);
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("error", returnStatusCode.value());
    errorDetails.put("description", message);
    return new ResponseEntity<>(errorDetails, returnStatusCode);
  }
}
