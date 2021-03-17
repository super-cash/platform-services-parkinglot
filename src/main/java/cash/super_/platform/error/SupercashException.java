package cash.super_.platform.error;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class SupercashException extends RuntimeException {

    @JsonProperty(value = "error_code")
    private SupercashErrorCode errorCode = SupercashErrorCode.NO_ERROR;

    @JsonProperty(value = "additional_error_code")
    private HttpStatus additionalErrorCode = HttpStatus.INTERNAL_SERVER_ERROR;

    @JsonProperty(value = "additional_description")
    private String additionalDescription = "";

    public SupercashException(SupercashErrorCode errorCode) {
        super(errorCode.description());
        this.errorCode = errorCode;
    }

    public SupercashException(SupercashErrorCode errorCode, String additionalDescription) {
        super(errorCode.description());
        this.errorCode = errorCode;
        this.additionalDescription = additionalDescription;
    }

    public SupercashException(SupercashErrorCode errorCode, HttpStatus additionalErrorCode) {
        super(errorCode.description());
        this.errorCode = errorCode;
        this.additionalErrorCode = additionalErrorCode;
    }

    public SupercashException(SupercashErrorCode errorCode, HttpStatus additionalErrorCode,
                              String additionalDescription) {
        super(errorCode.description());
        this.errorCode = errorCode;
        this.additionalErrorCode = additionalErrorCode;
        this.additionalDescription = additionalDescription;
    }

    @JsonGetter(value = "error_code")
    public Integer getErrorCode() {
        return errorCode.value();
    }

    public SupercashErrorCode getErrorCodeAsSupercashErrorCode() {
        return errorCode;
    }

    @JsonSetter(value = "error_code")
    public void setErrorCode(Integer errorCode) {
        this.errorCode = SupercashErrorCode.valueOf(errorCode);
    }

    public void setErrorCodeAsSupercashErrorCode(SupercashErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @JsonGetter(value = "description")
    public String getDescription() { return errorCode.description(); }

    @JsonGetter(value = "additional_error_code")
    public Integer getAdditionalErrorCode() {
        return additionalErrorCode.value();
    }

    public HttpStatus getAdditionalErrorCodeAsHttpStatus() {
        return additionalErrorCode;
    }

    @JsonSetter(value = "additional_error_code")
    public void setAdditionalErrorCode(Integer additionalErrorCode) {
        this.additionalErrorCode = HttpStatus.valueOf(additionalErrorCode);
    }

    public void setAdditionalErrorCodeAsHttpStatus(HttpStatus additionalErrorCode) {
        this.additionalErrorCode = additionalErrorCode;
    }

    public String getAdditionalDescription() {
        return additionalDescription;
    }

    public void setAdditionalDescription(String additionalDescription) {
        this.additionalDescription = additionalDescription;
    }

    public Map<String, Object> getHttpResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("error_code", errorCode.value());
        response.put("description", errorCode.description());
        response.put("additional_error_code", additionalErrorCode.value());
        response.put("additional_description", additionalDescription);
        return response;
    }

    @Override
    public String toString() {
        return "SupercashException{" +
                "errorCode=" + errorCode.value() +
                ", description='" + errorCode.description() + '\'' +
                ", additionalErrorCode=" + additionalErrorCode.value() +
                ", additionalDescription='" + additionalDescription + '\'' +
                '}';
    }
}
