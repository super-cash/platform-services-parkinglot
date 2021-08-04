package cash.super_.platform.client.payment.error;

import java.util.List;

public class PaymentServiceException {

    public List<PaymentServiceError> errors;

    public String url;

    public String method;

    public List<PaymentServiceError> getErrors() {
        return errors;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }
}
