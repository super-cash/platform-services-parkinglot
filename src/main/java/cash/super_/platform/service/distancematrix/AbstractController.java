package cash.super_.platform.service.distancematrix;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import cash.super_.platform.service.configuration.DistanceMatrixProperties;

/**
 * Abstract controller error that can be reused by other services.
 * Based on https://howtodoinjava.com/spring-boot2/spring-rest-request-validation/
 * @author marcellodesales
 *
 */
public abstract class AbstractController extends ResponseEntityExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractController.class);

  @Autowired
  protected DistanceMatrixProperties properties;

  /**
   * @return The default headers for all Controller Calls
   */
  protected HttpHeaders makeDefaultHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("api-version", properties.getApiVersion());
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
  @ExceptionHandler(Exception.class)
  public final ResponseEntity<Object> handleAllExceptions(Exception error, WebRequest request) {
    LOG.error("Error handling the request: ", error);
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("error", (long) 500);
    errorDetails.put("description", error.getMessage());
    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
