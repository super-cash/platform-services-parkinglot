package cash.super_.platform.error.thirdparty;

import java.util.List;

public class PagarmeErrorResult {

    public List<PagarmeError> errors;

    public String url;

    public String method;

    public List<PagarmeError> getErrors() {
        return errors;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }
}
