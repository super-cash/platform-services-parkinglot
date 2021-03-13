package cash.super_.platform.service.pagarme.transactions.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionRequest extends Transaction {

    @JsonProperty(value = "api_key")
    private String apiKey;

    /**
     * Informações do cartão do cliente criptografadas em sua aplicação. OBS: apenas para transações de Cartão de
     * crédito você deve passar o card_hash ou card_id. Caso inclua os dados do cartão diretamente pelo código, esse
     * campo torna-se dispensável.
     */
    @JsonProperty(value = "card_hash")
    private String cardHash;

    /**
     * Ao realizar uma transação, retornamos o card_id do cartão, para que nas próximas transações ele possa ser
     * utilizado como forma de identificar os dados de pagamento. Exemplo de utilização: One-click buy. OBS: apenas para
     * transações de Cartão de crédito você deve passar o card_hash ou card_id. Caso inclua os dados do cartão
     * diretamente pelo código, esse campo torna-se dispensável.
     */
    @JsonProperty(value = "card_id")
    private String cardId;

    /**
     * Data de validade do cartão no formato MMAA. OBS: apenas para transações de Cartão de crédito você deve passar o
     * <code>card_expiration_date</code>.
     */
    @JsonProperty(value = "card_expiration_date")
    private String cardExpirationDate;

    /**
     * Número do cartão. OBS: apenas para transações de Cartão de crédito você deve passar o <code>card_number</code.
     */
    @JsonProperty("card_number")
    private String cardNumber;

    /**
     * Código verificador do cartão. OBS: O <code>card_cvv</code> deve ser passado somente para transações de Cartão de
     * crédito. Esse parâmetro também pode ser passado em conjunto com o <code>card_id</code>, para validarmos seu CVV
     * na criação da transação.
     */
    @JsonProperty("card_cvv")
    private String cardCvv;

    /**
     * Nome do portador do cartão.
     */
    @JsonProperty(value = "card_holder_name")
    private String cardHolderName;

    /**
     * Informações de documentos do comprador. Obrigatório com o antifraude habilitado.
     */
    private List<Document> documents;

    /**
     * Utilize false caso queira manter o processamento síncrono de uma transação. Ou seja, a resposta da transação é
     * recebida na hora.
     */
    private Boolean async = false;

    /**
     * Após a autorização de uma transação, você pode escolher se irá capturar ou adiar a captura do valor. Caso opte
     * por postergar a captura, atribua o valor false.
     */
    private Boolean capture = true;

    /**
     * Métodos de captura possíveis: <code>emv</code>, <code>magstripe</code> e
     * <code>ecommerce</code>
     */
    @JsonProperty(value = "capture_method")
    private Transaction.CaptureMethod captureMethod;

    /**
     * Campo instruções do boleto. Máximo de 255 caracteres.
     */
    @JsonProperty("boleto_instructions")
    private String boletoInstructions;

    /**
     * Boleto Fine
     */
    @JsonProperty("boleto_fine")
    private BoletoFine boletoFine;

    /**
     * Boleto Interest
     */
    @JsonProperty("boleto_interest")
    private BoletoInterest boletoInterest;

    /**
     * Combinação de valores que define as regras do boleto emitido. Valores possíveis: 'strict_expiration_date'
     * (restringe o pagamento para até a data de vencimento e apenas o valor exato do documento), 'no_strict' (permite
     * pagamento após o vencimento e valores diferentes do impresso).
     */
    @JsonProperty("boleto_rules")
    private List<BoletoRule> boletoRules;

    /**
     * Data e hora do dispositivo que está efetuando a transação. Deve ser enviado no seguinte formato:
     * yyyy-MM-dd'T'HH:mm:ss'Z. Por exemplo: 2017-10-31T14:53:00.000Z. OBS.: este campo é necessário para transações de
     * mundo físico (com método de captura EMV e Magstripe).
     */
//    temporarily ignored due to (de)serialization issues
//    @JsonProperty("local_time")
//    @JsonInclude(JsonInclude.Include.NON_NULL
//    private LocalDateTime localTime = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));

    public TransactionRequest() {
        this.uuid = UUID.randomUUID();
    }

    public TransactionRequest(UUID uuid) {
        this.uuid = uuid;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getCardHash() {
        return cardHash;
    }

    public void setCardHash(String cardHash) {
        this.cardHash = cardHash;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardExpirationDate() {
        return cardExpirationDate;
    }

    public void setCardExpirationDate(String cardExpirationDate) {
        this.cardExpirationDate = cardExpirationDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardCvv() {
        return cardCvv;
    }

    public void setCardCvv(String cardCvv) {
        this.cardCvv = cardCvv;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public Boolean getCapture() {
        return capture;
    }

    public void setCapture(Boolean capture) {
        this.capture = capture;
    }

    public CaptureMethod getCaptureMethod() {
        return captureMethod;
    }

    public void setCaptureMethod(CaptureMethod captureMethod) {
        this.captureMethod = captureMethod;
    }

    public String getBoletoInstructions() {
        return boletoInstructions;
    }

    public void setBoletoInstructions(String boletoInstructions) {
        this.boletoInstructions = boletoInstructions;
    }

    public BoletoFine getBoletoFine() {
        return boletoFine;
    }

    public void setBoletoFine(BoletoFine boletoFine) {
        this.boletoFine = boletoFine;
    }

    public BoletoInterest getBoletoInterest() {
        return boletoInterest;
    }

    public void setBoletoInterest(BoletoInterest boletoInterest) {
        this.boletoInterest = boletoInterest;
    }

    public List<BoletoRule> getBoletoRules() {
        return boletoRules;
    }

    public void setBoletoRules(List<BoletoRule> boletoRules) {
        this.boletoRules = boletoRules;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "TransactionRequest{" +
                super.toString() +
                ", apiKey='" + apiKey + '\'' +
                ", cardHash='" + cardHash + '\'' +
                ", cardId='" + cardId + '\'' +
                ", cardExpirationDate='" + cardExpirationDate + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardCvv='" + cardCvv + '\'' +
                ", documents=" + documents +
                ", async=" + async +
                ", capture=" + capture +
                ", captureMethod=" + captureMethod +
                ", boletoInstructions='" + boletoInstructions + '\'' +
                ", boletoFine=" + boletoFine +
                ", boletoInterest=" + boletoInterest +
                ", boletoRules=" + boletoRules +
                '}';
    }
}
