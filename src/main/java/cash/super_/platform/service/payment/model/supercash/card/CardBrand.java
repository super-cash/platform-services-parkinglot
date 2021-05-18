package cash.super_.platform.service.payment.model.supercash.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Cards Brands
 */
public enum CardBrand {

    /**
     * em ambiente de testes
     */
    @JsonProperty("development")
    DEVELOPMENT("Development"),

    @JsonProperty("unknown")
    UNKNOWN("Unknown"),

    @JsonProperty("visa")
    VISA("Visa"),

    @JsonProperty("mastercard")
    MASTERCARD("Mastercard"),

    @JsonProperty("americanexpress")
    AMERICANEXPRESS("American Express"),

    @JsonProperty("aura")
    AURA("Aura"),

    @JsonProperty("banese")
    BANESE("Banese"),

    @JsonProperty("cabal")
    CABAL("Cabal"),

    @JsonProperty("diners")
    DINERS("Diners"),

    @JsonProperty("discover")
    DISCOVER("Discover"),

    @JsonProperty("elo")
    ELO("Elo"),

    @JsonProperty("fort")
    FORT("Fort"),

    @JsonProperty("grandcard")
    GRANDCARD("Grandcard"),

    @JsonProperty("hipercard")
    HIPERCARD("Hipercard"),

    @JsonProperty("jcb")
    JCB("JCB"),

    @JsonProperty("personalcard")
    PERSONALCARD("Personal card"),

    @JsonProperty("sorocred")
    SOROCRED("Sorocred"),

    @JsonProperty("valecard")
    VALECARD("Valecard");

    private String value;

    private CardBrand(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonIgnore
    private static Map<CardBrand, String> cardsRegex = new HashMap<>() {{
        put(CardBrand.VISA, "/^4[0-9]{12}(?:[0-9]{3})/");
        put(CardBrand.MASTERCARD, "/^(5[1-5][0-9]{14}|2(22[1-9][0-9]{12}|2[3-9][0-9]{13}|[3-6][0-9]{14}|7[0-1][0-9]{13}|720[0-9]{12}))$/");
        put(CardBrand.AMERICANEXPRESS, "/^3[47][0-9]{13}/");
        put(CardBrand.AURA, "'^507860'");
        put(CardBrand.BANESE, "'^636117'");
        put(CardBrand.CABAL, "'(60420[1-9]|6042[1-9][0-9]|6043[0-9]{2}|604400)'");
        put(CardBrand.DINERS, "'(36[0-8][0-9]{3}|369[0-8][0-9]{2}|3699[0-8][0-9]|36999[0-9])");
        put(CardBrand.DISCOVER, "/^6(?:011|5[0-9]{2})[0-9]{12}/");
        put(CardBrand.ELO, "^4011(78|79)|^43(1274|8935)|^45(1416|7393|763(1|2))|^504175|^627780|^63(6297|6368|6369)|(65003[5-9]|65004[0-9]|65005[01])|(65040[5-9]|6504[1-3][0-9])|(65048[5-9]|65049[0-9]|6505[0-2][0-9]|65053[0-8])|(65054[1-9]|6505[5-8][0-9]|65059[0-8])|(65070[0-9]|65071[0-8])|(65072[0-7])|(65090[1-9]|6509[1-6][0-9]|65097[0-8])|(65165[2-9]|6516[67][0-9])|(65500[0-9]|65501[0-9])|(65502[1-9]|6550[34][0-9]|65505[0-8])|^(506699|5067[0-6][0-9]|50677[0-8])|^(509[0-8][0-9]{2}|5099[0-8][0-9]|50999[0-9])|^65003[1-3]|^(65003[5-9]|65004\\d|65005[0-1])|^(65040[5-9]|6504[1-3]\\d)|^(65048[5-9]|65049\\d|6505[0-2]\\d|65053[0-8])|^(65054[1-9]|6505[5-8]\\d|65059[0-8])|^(65070\\d|65071[0-8])|^65072[0-7]|^(65090[1-9]|65091\\d|650920)|^(65165[2-9]|6516[6-7]\\d)|^(65500\\d|65501\\d)|^(65502[1-9]|6550[3-4]\\d|65505[0-8])");
        put(CardBrand.FORT, "'^628167'");
        put(CardBrand.GRANDCARD, "'^605032'");
        put(CardBrand.HIPERCARD, "'^606282|^637095|^637599|^637568'");
        put(CardBrand.JCB, "/^(?:2131|1800|35\\d{3})\\d{11}/");
        put(CardBrand.PERSONALCARD, "'^636085'");
        put(CardBrand.SOROCRED, "'^627892|^636414'");
        put(CardBrand.VALECARD, "'^606444|^606458|^606482'");
    }};
}
