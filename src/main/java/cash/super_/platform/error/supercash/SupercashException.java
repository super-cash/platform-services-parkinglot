package cash.super_.platform.error.supercash;

import cash.super_.platform.error.model.SupercashExceptionModel;
import cash.super_.platform.error.supercash.SupercashErrorCode;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.http.HttpStatus;

public class SupercashException extends RuntimeException {

    @JsonProperty(value = "SupercashException")
    public SupercashExceptionModel SupercashExceptionModel = new SupercashExceptionModel();

    public SupercashException() {

    }

    public SupercashException(SupercashErrorCode errorCode) {
        super(errorCode.description());
        SupercashExceptionModel.setErrorCodeAsSupercashErrorCode(errorCode);
    }

    public SupercashException(String additionalDescription) {
        super(SupercashErrorCode.GENERAL_ERROR.description());
        SupercashExceptionModel.setErrorCodeAsSupercashErrorCode(SupercashErrorCode.GENERAL_ERROR);
        SupercashExceptionModel.setAdditionalDescription(additionalDescription);
    }

    public SupercashException(SupercashErrorCode errorCode, String additionalDescription) {
        super(errorCode.description());
        SupercashExceptionModel.setErrorCodeAsSupercashErrorCode(errorCode);
        SupercashExceptionModel.setAdditionalDescription(additionalDescription);
    }

    public SupercashException(SupercashErrorCode errorCode, HttpStatus additionalErrorCode) {
        super(errorCode.description());
        SupercashExceptionModel.setErrorCodeAsSupercashErrorCode(errorCode);
        SupercashExceptionModel.setAdditionalErrorCodeAsHttpStatus(additionalErrorCode);
    }

    public SupercashException(SupercashErrorCode errorCode, HttpStatus additionalErrorCode,
                              String additionalDescription) {
        super(errorCode.description());
        SupercashExceptionModel.setErrorCodeAsSupercashErrorCode(errorCode);
        SupercashExceptionModel.setAdditionalErrorCodeAsHttpStatus(additionalErrorCode);
        SupercashExceptionModel.setAdditionalDescription(additionalDescription);
    }

    public SupercashException addField(String key, Object value) {
        this.SupercashExceptionModel.addField(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "SupercashException{" +
                "errorCode=" + SupercashExceptionModel.getErrorCode() +
                ", description='" + SupercashExceptionModel.getDescription() + '\'' +
                ", additionalErrorCode=" + SupercashExceptionModel.getAdditionalErrorCodeAsHttpStatus().value() +
                ", additionalDescription='" + SupercashExceptionModel.getAdditionalDescription() + '\'' +
                '}';
    }
}
