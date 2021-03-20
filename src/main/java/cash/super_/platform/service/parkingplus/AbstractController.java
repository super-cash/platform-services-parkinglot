package cash.super_.platform.service.parkingplus;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.error.supercash.SupercashException;
import cash.super_.platform.error.supercash.SupercashInvalidValueException;
import cash.super_.platform.service.parkingplus.model.ParkingTicketStatus;
import cash.super_.platform.service.parkingplus.sales.ParkingPlusParkingSalesCachedProxyService;
import cash.super_.platform.service.parkingplus.ticket.ParkingPlusTicketStatusProxyService;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
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

  @Autowired
  private ParkingPlusParkingSalesCachedProxyService parkingSalesService;

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
   * @param request is the response object.
   * @throws IOException while sending the error back to the client.
   */
  @ExceptionHandler(value = {Exception.class, MissingRequestHeaderException.class})
  public final ResponseEntity<Object> handleAllExceptions(Exception error, WebRequest request) {
    LOG.error("Error handling the request: {}", request);
    LOG.error("Error cause: {}", error.getCause());

    if (error instanceof SupercashException) {
      return makeErrorResponse((SupercashException)error);
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

  private ResponseEntity<Object> makeErrorResponse(SupercashException errorCause) {
    return new ResponseEntity<>(errorCause,
            errorCause.SupercashExceptionModel.getAdditionalErrorCodeAsHttpStatus());
  }

  /**
   * Verifies if the request is valid based on the inputs
   * TODO: Make sure we can remove the headerUserId and still map it to the loggers
   *
   * @param headerUserId
   * @param userId
   */
  protected void isRequestValid(String headerUserId, String userId) {
    if (!headerUserId.equals(userId)) {
      throw new SupercashInvalidValueException("UserID must be provided in both header and path.");
    }
  }

}