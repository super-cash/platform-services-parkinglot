package cash.super_.platform.service.parkinglot;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import cash.super_.platform.adapter.feign.SupercashSimpleException;
import cash.super_.platform.adapter.feign.SupercashRetryableException;
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
import cash.super_.platform.autoconfig.ParkingPlusServiceClientProperties;
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
  public static final String BASE_ENDPOINT = "/parking_lot";
  protected static final String TICKETS_ENDPOINT = BASE_ENDPOINT + "/tickets";

  @Autowired
  protected ParkingPlusServiceClientProperties properties;

  /**
   * @return The default headers for all Controller Calls
   */
  protected HttpHeaders makeDefaultHttpHeaders(Map<String, String> additionalHeaders) {
    HttpHeaders headers = new HttpHeaders();

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
   * @param request is the response object.
   * @throws IOException while sending the error back to the client.
   */
  @ExceptionHandler(value = {Exception.class, MissingRequestHeaderException.class})
  public final ResponseEntity<Object> handleAllExceptions(Exception error, WebRequest request) {
    LOG.error("Error handling the request:", error);

    if (error instanceof SupercashRetryableException) {
      return makeErrorResponse((SupercashSimpleException) error.getCause());
    }

    if (error instanceof SupercashSimpleException) {
      return makeErrorResponse((SupercashSimpleException)error);
    }

    if (error instanceof IllegalArgumentException) {
      return makeErrorResponse(error, error.getMessage(), HttpStatus.BAD_REQUEST);
    }
    if (error instanceof IllegalStateException) {
      if (error.getMessage().contains("There's no payments for user=")) {
        return makeErrorResponse(error, error.getMessage(), HttpStatus.NOT_FOUND);
      }
    }
    if (error instanceof FeignException.NotFound) {
      FeignException.NotFound feignError = (FeignException.NotFound)error;
      String message = feignError.getMessage();
      if (message.contains("errorCode") && message.contains(":31")) {
        return makeErrorResponse(error, message, HttpStatus.BAD_REQUEST);
      } else return makeErrorResponse(error, message, HttpStatus.NOT_FOUND);
    }
    if (error instanceof FeignException.Forbidden) {
      FeignException.Forbidden feignError = (FeignException.Forbidden)error;
      String message = feignError.getMessage();
      if (message.contains("Valor Inválido")) {
        return makeErrorResponse(error, message, HttpStatus.BAD_REQUEST);
      }
      if (message.contains("id da promoção não existe!")) {
        return makeErrorResponse(error, message, HttpStatus.BAD_REQUEST);
      }
      if (message.contains("não encontrado") || message.contains("não existe")) {
        return makeErrorResponse(error, message, HttpStatus.NOT_FOUND);
      }
      if (message.contains("possui um desconto aplicado") || message.contains("Transação já realizada.")) {
        return makeErrorResponse(error, message, HttpStatus.FORBIDDEN);
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
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("error_code", returnStatusCode.value());
    errorDetails.put("description", message);
    return new ResponseEntity<>(errorDetails, returnStatusCode);
  }

  private ResponseEntity<Object> makeErrorResponse(SupercashSimpleException errorCause) {
    return new ResponseEntity<>(errorCause,
            errorCause.SupercashExceptionModel.getAdditionalErrorCodeAsHttpStatus());
  }

}