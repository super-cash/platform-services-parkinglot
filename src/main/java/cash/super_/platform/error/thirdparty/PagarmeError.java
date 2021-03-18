package cash.super_.platform.error.thirdparty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PagarmeError {

    public String type;

    @JsonProperty("parameter_name")
    public Object parameterName;

    public String message;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParameterName() {
        return parameterName.toString();
    }

    public void setParameterName(Object parameterName) {
        this.parameterName = parameterName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
