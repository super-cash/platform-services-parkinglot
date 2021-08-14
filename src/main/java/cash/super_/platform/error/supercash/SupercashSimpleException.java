package cash.super_.platform.error.supercash;

import cash.super_.platform.error.SupercashExceptionModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.http.HttpStatus;

public class SupercashSimpleException extends RuntimeException {

    @JsonProperty(value = "SupercashException")
    public SupercashExceptionModel SupercashExceptionModel = new SupercashExceptionModel();

    public SupercashSimpleException() {

    }

    public SupercashSimpleException(SupercashErrorCode errorCode) {
        super(errorCode.description());
        SupercashExceptionModel.setErrorCodeAsSupercashErrorCode(errorCode);
    }

    public SupercashSimpleException(String additionalDescription) {
        super(SupercashErrorCode.GENERIC_ERROR.description());
        SupercashExceptionModel.setErrorCodeAsSupercashErrorCode(SupercashErrorCode.GENERIC_ERROR);
        SupercashExceptionModel.setAdditionalDescription(additionalDescription);
    }

    public SupercashSimpleException(SupercashErrorCode errorCode, String additionalDescription) {
        super(errorCode.description());
        SupercashExceptionModel.setErrorCodeAsSupercashErrorCode(errorCode);
        SupercashExceptionModel.setAdditionalDescription(additionalDescription);
    }

    public SupercashSimpleException(SupercashErrorCode errorCode, HttpStatus additionalErrorCode) {
        super(errorCode.description());
        SupercashExceptionModel.setErrorCodeAsSupercashErrorCode(errorCode);
        SupercashExceptionModel.setAdditionalErrorCodeAsHttpStatus(additionalErrorCode);
    }

    public SupercashSimpleException(SupercashErrorCode errorCode, HttpStatus additionalErrorCode,
                                    String additionalDescription) {
        super(errorCode.description());
        SupercashExceptionModel.setErrorCodeAsSupercashErrorCode(errorCode);
        SupercashExceptionModel.setAdditionalErrorCodeAsHttpStatus(additionalErrorCode);
        SupercashExceptionModel.setAdditionalDescription(additionalDescription);
    }

    public void addField(String key, Object value) {
        this.SupercashExceptionModel.addField(key, value);
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
