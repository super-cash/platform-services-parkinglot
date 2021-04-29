package cash.super_.platform.service.payment.model;

public enum BoletoRule {
    STRICT_EXPIRATION_DATE("strict_expiration_date"),
    NO_STRICT("no_strict");

    private String value;

    BoletoRule(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }


}
