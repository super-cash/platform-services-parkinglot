package cash.super_.platform.error.supercash;

import cash.super_.platform.error.model.SupercashExceptionModel;
import cash.super_.platform.error.thirdparty.WPSException;
import cash.super_.platform.utils.JsonUtil;
import com.google.common.io.CharStreams;
import feign.Response;
import feign.Response.Body;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class SupercashErrorDecoder implements ErrorDecoder {

  protected static final Logger LOG = LoggerFactory.getLogger(SupercashErrorDecoder.class);

  @Override
  public Exception decode(String methodKey, Response response) {
    Reader reader = null;
    Body body = response.body();
    if (body != null) {
      String responseBody = "";
      try {
        reader = response.body().asReader(StandardCharsets.UTF_8);
        responseBody = CharStreams.toString(reader);
      } catch (IOException e) {
          LOG.error("Error decoding response exception message: {}", e.getMessage());
          LOG.error("Response object is: {}", response);
          return e;
      } finally {
        try {
            if (reader != null) reader.close();

        } catch (IOException e) {
            return e;
        }
      }

      if (responseBody.contains("mensagem") && responseBody.contains("errorCode")) {
        WPSException wpsException = JsonUtil.toObject(responseBody, WPSException.class);
        SupercashException supercashException = new SupercashThirdPartySystemException();
        supercashException.SupercashExceptionModel.addField("third_party_message", wpsException.getMessage());
        supercashException.SupercashExceptionModel.addField("third_party_error_code", wpsException.getErrorCode());
        return supercashException;
      }

      SupercashExceptionModel supercashExceptionModel = JsonUtil.toObject(responseBody, SupercashExceptionModel.class);
      SupercashException supercashException = new SupercashException();
      supercashException.SupercashExceptionModel = supercashExceptionModel;
      return supercashException;
      // An alternative is to swith the response.status()
      // switch(response.status()) {
      //   case 300:
      //      create specific exception
    }

    return new Default().decode(methodKey, response);
  }
}
