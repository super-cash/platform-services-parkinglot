package cash.super_.platform.error.model;

import cash.super_.platform.error.supercash.SupercashErrorCode;
import com.fasterxml.jackson.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"stackTrace", "cause"})
public class SupercashExceptionModel {

    @JsonProperty(value = "error_code")
    private SupercashErrorCode errorCode = SupercashErrorCode.NO_ERROR;

    @JsonProperty(value = "additional_error_code")
    private HttpStatus additionalErrorCode = HttpStatus.OK;

    @JsonProperty(value = "additional_description")
    private String additionalDescription = "";

    @JsonProperty(value = "additional_fields")
    private Map<String, Object> additionalFields = new HashMap<>();

    @JsonGetter(value = "error_code")
    public Integer getErrorCode() {
        return errorCode.value();
    }

    @JsonIgnore
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

    @JsonIgnore
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

    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }

    public void setAdditionalFields(Map<String, Object> additionalFields) {
        this.additionalFields = additionalFields;
    }

    public void addField(String key, Object value) {
        additionalFields.put(key, value);
    }

    @Override
    public String toString() {
        return "SupercashException{" +
                "errorCode=" + errorCode +
                ", additionalErrorCode=" + additionalErrorCode +
                ", additionalDescription='" + additionalDescription + '\'' +
                ", additionalFields=" + additionalFields +
                '}';
    }
}
