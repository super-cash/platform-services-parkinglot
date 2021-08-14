package cash.super_.platform.error;

import cash.super_.platform.error.parkinglot.SupercashErrorCode;
import com.fasterxml.jackson.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"stackTrace", "cause"})
public class SupercashExceptionModel {

    @JsonProperty(value = "error_code")
    private SupercashErrorCode errorCode = SupercashErrorCode.GENERIC_ERROR;

    private String description;

//    @JsonProperty(value = "additional_error_code")
    @JsonIgnore
    private HttpStatus additionalErrorCode = HttpStatus.INTERNAL_SERVER_ERROR;

    @JsonProperty(value = "additional_description")
    private String additionalDescription = "";

    @JsonProperty(value = "additional_fields")
    private Map<String, Object> additionalFields = new HashMap<>();

    public SupercashExceptionModel () {
        this.description = errorCode.description();
    }

    @JsonGetter(value = "error_code")
    public Integer getErrorCode() {
        return errorCode.value();
    }

    @JsonSetter(value = "error_code")
    public void setErrorCode(Integer errorCode) {
        this.errorCode = SupercashErrorCode.valueOf(errorCode);
    }

    @JsonIgnore
    public SupercashErrorCode getErrorCodeAsSupercashErrorCode() {
        return errorCode;
    }

    public void setErrorCodeAsSupercashErrorCode(SupercashErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getDescription() {
        return errorCode.description();
    }

    public void setDescription(String description) {

    }

//    @JsonGetter(value = "additional_error_code")
    @JsonIgnore
    public Integer getAdditionalErrorCode() {
        return additionalErrorCode.value();
    }

    @JsonIgnore
    public HttpStatus getAdditionalErrorCodeAsHttpStatus() {
        return additionalErrorCode;
    }

//    @JsonSetter(value = "additional_error_code")
    @JsonIgnore
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
                ", description='" + errorCode.description() + '\'' +
                ", additionalErrorCode=" + additionalErrorCode +
                ", additionalDescription='" + additionalDescription + '\'' +
                ", additionalFields=" + additionalFields +
                '}';
    }
}
