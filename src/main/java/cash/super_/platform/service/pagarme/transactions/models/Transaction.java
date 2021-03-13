package cash.super_.platform.service.pagarme.transactions.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.beans.Transient;
import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {

    /**
     * Supercash transaction ID in the database.
     */
    @JsonIgnore
    private Long transactionId;

    /**
     * Supercash internal ID, defined when a request enter for payment.
     */
    @JsonIgnore
    protected UUID uuid;

    /**
     * Número identificador da transação.
     */
    private Integer id;

    /**
     * Valor a ser cobrado. Deve ser passado em centavos. Ex: R$ 10.00 = 1000. Deve ser no mínimo 1 real (100).
     */
    private Integer amount;

    /**
     * Métodos de pagamento possíveis: <code>credit_card</code>,
     * <code>boleto</code> e <code>debit_card</code>
     */
    @JsonProperty(value = "payment_method")
    private Transaction.PaymentMethod paymentMethod;

    /**
     * URL (endpoint) do sistema integrado a Pagar.me que receberá as respostas
     * a cada atualização do processamento da transação
     */
    @JsonProperty(value = "postback_url")
    private String postbackUrl;

    /**
     * Número de parcelas da transação, sendo mínimo: 1 e Máximo: 12. OBS: Se o pagamento for boleto, o padrão é 1.
     */
    @Min(1)
    @Max(12)
    private Integer installments;

    /**
     * Data de expiração do boleto (em ISODate).
     */
    @JsonProperty(value = "boleto_expiration_date")
    private String boletoExpirationDate;

    /**
     * Texto que irá aparecer na fatura do cliente depois do nome da loja.
     * <b>OBS:</b> Limite de 13 caracteres.
     */
    @JsonProperty(value = "soft_descriptor")
    private String softDescriptor = "*Supercash";

    /**
     * Objeto com dados do cliente. Obrigatório com o antifraude habilitado.
     */
    private Customer customer;

    /**
     * Dados de cobrança da transação. Obrigatório com o antifraude habilitado.
     */
    private Billing billing;

    /**
     * Dados de envio do que foi comprado. Deve ser preenchido no caso de venda de bem físico.
     */
    private Shipping shipping;

    /**
     * Dados sobre os produtos comprados. Obrigatório com o antifraude habilitado.
     */
    private List<Item> items;

    /**
     * Objeto com dados adicionais do cliente/produto/serviço vendido
     */
    private Map<String, String> metadata = new HashMap<>();

    /**
     * Objeto com as regras de split definidas para essa transação.
     */
    @JsonProperty(value = "split_rules")
    private List<SplitRule> splitRules;

    /**
     * Valor único que identifica a transação para permitir uma nova tentativa de requisição com a segurança de que a
     * mesma operação não será executada duas vezes acidentalmente.
     */
    @JsonProperty(value = "reference_key")
    private String referenceKey;

    @JsonProperty(value = "pix_qr_code")
    private String pixQrCode;

    @JsonProperty(value = "pix_expiration_date")
    private String pixExpirationDate;

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

    public Transaction() {
        this.uuid = UUID.randomUUID();
    }

    public Long getTransactionId() { return transactionId; }

    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public UUID getUuid() { return uuid; }

    public void setUuid(UUID uuid) { this.uuid = uuid; }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPostbackUrl() {
        return postbackUrl;
    }

    public void setPostbackUrl(String postbackUrl) {
        this.postbackUrl = postbackUrl;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public String getBoletoExpirationDate() {
        return boletoExpirationDate;
    }

    public void setBoletoExpirationDate(String boletoExpirationDate) {
        boletoExpirationDate = boletoExpirationDate;
    }

    public String getSoftDescriptor() {
        return softDescriptor;
    }

    public void setSoftDescriptor(String softDescriptor) {
        this.softDescriptor = softDescriptor;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Billing getBilling() {
        return billing;
    }

    public void setBilling(Billing billing) {
        this.billing = billing;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(String key, String value) {
        this.metadata.put(key, value);
    }

    public void removeMetadata(String key) {
        this.metadata.remove(key);
    }

    public List<SplitRule> getSplitRules() {
        return splitRules;
    }

    public void setSplitRules(List<SplitRule> splitRules) {
        this.splitRules = splitRules;
    }

    public String getReferenceKey() {
        return referenceKey;
    }

    public void setReferenceKey(String referenceKey) {
        this.referenceKey = referenceKey;
    }

    public static CardBrand getBrand(String creditCardNumber) {
        Iterator iterator = cardsRegex.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<CardBrand, String> keyValue = (Map.Entry) iterator.next();
            if (creditCardNumber.matches(keyValue.getValue())) {
                return keyValue.getKey();
            }
        }
        return CardBrand.UNKNOWN;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "uuid=" + uuid +
                ", transactionId=" + transactionId +
                ", id=" + id +
                ", amount=" + amount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid);
    }

    /**
     * Adquirente responsável pelo processamento da transação.
     */
    public enum AcquirerName {

        /**
         * em ambiente de testes
         */
        @JsonProperty("development")
        DEVELOPMENT,

        /**
         * adquirente Pagar.me
         */
        @JsonProperty("pagarme")
        PAGARME,

        @JsonProperty("stone")
        STONE,

        @JsonProperty("cielo")
        CIELO,

        @JsonProperty("rede")
        REDE,

        @JsonProperty("mundipagg")
        MUNDIPAGG

    }

    /**
     * Cards Brands
     */
    public enum CardBrand {

        /**
         * em ambiente de testes
         */
        @JsonProperty("development")
        DEVELOPMENT("development"),

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
        PERSONALCARD("Personalcard"),

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
    }

    public enum Event {

        @JsonProperty("transaction_status_changed")
        TRANSACTION_STATUS_CHANGED

    }

    /**
     * Método de pagamento
     */
    public enum PaymentMethod {

        @JsonProperty("credit_card")
        CREDIT_CARD,

        @JsonProperty("boleto")
        BOLETO,

        @JsonProperty("debit_card")
        DEBIT_CARD,

        @JsonProperty("pix")
        PIX
    }

    public enum CaptureMethod {

        @JsonProperty("emv")
        EMV,

        @JsonProperty("magstripe")
        MAGSTRIPE,

        @JsonProperty("ecommerce")
        ECOMMERCE
    }

//    public enum MoipStatus {
//        CREATED,
//        WAITING,
//        PAID,
//        NOT_PAID,
//        REVERTED
//    }

    /**
     * Quando uma transação é criada, ela inicialmente é retornada com o status
     * {@link #PROCESSING}.
     */
    public enum Status {

//        PROCESSING(MoipStatus.CREATED),
//        AUTHORIZED(MoipStatus.WAITING),
//        PAID(MoipStatus.PAID),
//        REFUNDED(MoipStatus.REVERTED),
//        WAITING_PAYMENT(MoipStatus.WAITING),
//        PENDING_REFUND(MoipStatus.PAID),
//        REFUSED(MoipStatus.NOT_PAID);

        /**
         * Transação sendo processada.
         */
        @JsonProperty("processing")
        PROCESSING,

        /**
         * Transação autorizada. Cliente possui saldo na conta e este valor foi
         * reservado para futura captura, que deve acontecer em no máximo 5
         * dias. Caso a transação <b>não seja capturada</b>, a autorização é
         * cancelada automaticamente.
         */
        @JsonProperty("authorized")
        AUTHORIZED,

        /**
         * Transação paga (autorizada e capturada).
         */
        @JsonProperty("paid")
        PAID,

        /**
         * Transação estornada.
         */
        @JsonProperty("refunded")
        REFUNDED,

        /**
         * Transação aguardando pagamento (status para transações criadas com
         * boleto bancário ou pix).
         */
        @JsonProperty("waiting_payment")
        WAITING_PAYMENT,

        /**
         * Transação paga com boleto aguardando para ser estornada.
         */
        @JsonProperty("pending_refund")
        PENDING_REFUND,

        /**
         * Transação não autorizada.
         */
        @JsonProperty("refused")
        REFUSED,

        /**
         * Transação sofreu chargeback.
         */
        @JsonProperty("chargedback")
        CHARGEDBACK;

//        @JsonIgnore
//        private MoipStatus moipStatus;

//        TransactionStatus(MoipStatus moipStatus) {
//            this.moipStatus = moipStatus;
//        }
//
//        public MoipStatus toMoipStatus() {
//            return moipStatus;
//        }
    }

    /**
     * Motivo/agente responsável pela validação ou anulação da transação.
     */
    public enum StatusReason {

        @JsonProperty("acquirer")
        ACQUIRER,

        @JsonProperty("antifraud")
        ANTIFRAUD,

        @JsonProperty("internal_error")
        INTERNAL_ERROR,

        @JsonProperty("no_acquirer")
        NO_ACQUIRER,

        @JsonProperty("acquirer_timeout")
        ACQUIRER_TIMEOUT

    }

}
