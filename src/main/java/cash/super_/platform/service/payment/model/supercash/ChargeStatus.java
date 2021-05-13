package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Status enum
 */
public enum ChargeStatus {

    /**
     * AUTHORIZED
     */
    @JsonProperty("AUTHORIZED")
    AUTHORIZED,

    /**
     * PAID
     */
    @JsonProperty("PAID")
    PAID,

    /**
     * WAITING (only for boleto)
     */
    @JsonProperty("WAITING")
    WAITING,

    /**
     * DECLINED
     */
    @JsonProperty("DECLINED")
    DECLINED,

    /**
     * CANCELED
     */
    @JsonProperty("CANCELED")
    CANCELED;

}