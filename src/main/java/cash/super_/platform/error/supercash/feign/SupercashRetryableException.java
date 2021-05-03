package cash.super_.platform.error.supercash.feign;

import cash.super_.platform.error.supercash.SupercashSimpleException;
import feign.Request;
import feign.RetryableException;

import java.util.Date;

public class SupercashRetryableException extends RetryableException {

    public SupercashRetryableException(int status, String message,
                                       Request.HttpMethod httpMethod, SupercashSimpleException cause,
                                       Date retryAfter, Request request) {
        super(status, message, httpMethod, cause, retryAfter, request);
    }

    public SupercashRetryableException(int status, String message, Request.HttpMethod httpMethod, Date retryAfter,
                                       Request request) {
        super(status, message, httpMethod, retryAfter, request);
    }
}
