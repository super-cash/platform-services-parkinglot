package cash.super_.platform.error.thirdparty;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class WPSException extends RuntimeException {

    private Integer errorCode = 0;

    @JsonProperty(value = "mensagem")
    private String message = "";

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getHttpResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("error_code", errorCode);
        response.put("description", message);
        response.put("additional_error_code", "");
        response.put("additional_description", "");
        return response;
    }

    @Override
    public String toString() {
        return "WPSException{" +
                "errorCode=" + errorCode +
                ", description='" + message + "'" +
                ", additionalErrorCode=" +
                ", additionalDescription=''" +
                '}';
    }
}
