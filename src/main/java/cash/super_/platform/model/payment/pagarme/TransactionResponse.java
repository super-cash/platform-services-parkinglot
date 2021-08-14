package cash.super_.platform.model.payment.pagarme;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse extends Transaction {

    /**
     * Nome do tipo do objeto criado/modificado.
     */
    private String object;

    /**
     * Para cada atualização no processamento da transação, esta propriedade
     * será alterada, e o objeto <code>transaction</code> retornado como
     * resposta através da sua URL de postback ou após o término do
     * processamento da ação atual.
     */
    private Status status;

    /**
     * Motivo pelo qual a transação foi recusada.
     */
    private String refuseReason;

    /**
     * Motivo/agente responsável pela validação ou anulação da transação.
     */
    @JsonProperty(value = "status_reason")

    private StatusReason statusReason;

    /**
     * Adquirente responsável pelo processamento da transação.
     */
    @JsonProperty(value = "acquirer_name")
    private AcquirerName acquirerName;

    /**
     * ID da adquirente responsável pelo processamento da transação.
     */
    @JsonProperty(value = "acquirer_id")
    private String acquirerId;

    /**
     * Mensagem de resposta do adquirente referente ao status da transação.
     */
    @JsonProperty(value = "acquirer_response_code")
    private String acquirerResponseCode;

    /**
     * Código de autorização retornado pela bandeira.
     */
    @JsonProperty(value = "authorization_code")
    private String authorizationCode;

    /**
     * Código que identifica a transação no adquirente.
     */
    private String tid;

    /**
     * Código que identifica a transação no adquirente.
     */
    private String nsu;

    /**
     * Data de criação da transação no formato ISODate
     */
    @JsonProperty(value = "date_created")
    private Date dateCreated;

    /**
     * Data de atualização da transação no formato ISODate
     */
    @JsonProperty(value = "date_updated")
    private Date updatedAt;

    /**
     * Valor em centavos autorizado na transação, sempre menor ou igual a amount.
     */
    @JsonProperty(value = "authorized_amount")
    private Long authorizedAmount;

    /**
     * Valor em centavos autorizado na transação, sempre menor ou igual a amount.
     */
    @JsonProperty(value = "paid_amount")
    private Long paidAmount;

    /**
     * Valor em centavos estornado até o momento na transação, sempre menor ou igual a paidamount.
     */
    @JsonProperty(value = "refunded_amount")
    private Long refundedAmount;

    /**
     * Custo da transação para o lojista, envolvendo processamento e antifraude.
     */
    private Long cost;

    /**
     * Últimos 4 dígitos do cartão.
     */
    @JsonProperty(value = "card_last_digits")
    private String cardLastDigits;

    /**
     * Primeiros 4 dígitos do cartão.
     */
    @JsonProperty(value = "card_first_digits")
    private String cardFirstDigits;

    /**
     * Bandeira do cartão.
     */
    @JsonProperty(value = "card_brand")
    private String cardBrand;

    /**
     * Usado em transações EMV, define se a validação do cartão aconteceu online(com banco emissor), ou offline (através
     * do chip).
     */
    @JsonProperty(value = "card_pin_mode")
    private String cardPinMode;

    /**
     * Define qual foi a nota de antifraude atribuída a transação. Lembrando que por padrão, transações com score >= 95
     * são recusadas.
     */
    @JsonProperty(value = "antifraud_score")
    private String antifraudScore;

    /**
     * URL do boleto para impressão.
     */
    @JsonProperty(value = "boleto_url")
    private String boletoUrl;

    /**
     * Código de barras do boleto gerado na transação.
     */
    @JsonProperty(value = "boleto_barcode")
    private String boletoBarcode;

    /**
     * Mostra se a transação foi criada utilizando a API Key ou Encryption Key.
     */
    private String referer;

    /**
     * Caso essa transação tenha sido originada na cobrança de uma assinatura, o id desta será o valor dessa
     * propriedade.
     */
    @JsonProperty(value = "subscription_id")
    private Long subscriptionId;

    /**
     * Ip do solicitando do pagamento.
     */
    private String ip;

    /**
     * Objeto com dados usados na integração com antifraude.
     */
//    @JsonProperty(value = "antifraud_metadata")
//    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JoinColumn(name = "transaction_id", referencedColumnName="transaction_id")
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private List<AntifraudAnalysis> antifraudMetadata;

    /**
     * Valor único que identifica a sessão do usuário acessando o site.
     */
    private String session;

    @JsonProperty(value = "old_status")
    private Status oldStatus;

    @JsonProperty(value = "current_status")
    private Status currentStatus;

    @JsonProperty(value = "desired_status")
    private Status desiredStatus;

    public Object getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRefuseReason() {
        return refuseReason;
    }

    public void setRefuseReason(String refuseReason) {
        this.refuseReason = refuseReason;
    }

    public StatusReason getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(StatusReason statusReason) {
        this.statusReason = statusReason;
    }

    public AcquirerName getAcquirerName() {
        return acquirerName;
    }

    public void setAcquirerName(AcquirerName acquirerName) {
        this.acquirerName = acquirerName;
    }

    public String getAcquirerId() {
        return acquirerId;
    }

    public void setAcquirerId(String acquirerId) {
        this.acquirerId = acquirerId;
    }

    public String getAcquirerResponseCode() {
        return acquirerResponseCode;
    }

    public void setAcquirerResponseCode(String acquirerResponseCode) { this.acquirerResponseCode = acquirerResponseCode; }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getNsu() {
        return nsu;
    }

    public void setNsu(String nsu) {
        this.nsu = nsu;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getAuthorizedAmount() {
        return authorizedAmount;
    }

    public void setAuthorizedAmount(Long authorizedAmount) {
        this.authorizedAmount = authorizedAmount;
    }

    public Long getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Long paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Long getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(Long refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public String getCardLastDigits() {
        return cardLastDigits;
    }

    public void setCardLastDigits(String cardLastDigits) {
        this.cardLastDigits = cardLastDigits;
    }

    public String getCardFirstDigits() {
        return cardFirstDigits;
    }

    public void setCardFirstDigits(String cardFirstDigits) {
        this.cardFirstDigits = cardFirstDigits;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getCardPinMode() {
        return cardPinMode;
    }

    public void setCardPinMode(String cardPinMode) {
        this.cardPinMode = cardPinMode;
    }

    public String getAntifraudScore() {
        return antifraudScore;
    }

    public void setAntifraudScore(String antifraudScore) {
        this.antifraudScore = antifraudScore;
    }

    public String getBoletoUrl() {
        return boletoUrl;
    }

    public void setBoletoUrl(String boletoUrl) {
        this.boletoUrl = boletoUrl;
    }

    public String getBoletoBarcode() {
        return boletoBarcode;
    }

    public void setBoletoBarcode(String boletoBarcode) {
        this.boletoBarcode = boletoBarcode;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

//    public List<AntifraudAnalysis> getAntifraudMetadata() {
//        return antifraudMetadata;
//    }
//
//    public void setAntifraudMetadata(List<AntifraudAnalysis> antifraudMetadata) {
//        this.antifraudMetadata = antifraudMetadata;
//    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    @JsonIgnore
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("transaction_id", this.getTransactionId());
        summary.put("uuid", this.getUuid());
        summary.put("status", this.getStatus());
        Long i = this.getId();
        if (i != null) summary.put("gateway_transaction_id", i);
        i = this.getPaidAmount();
        summary.put("paid_amount", i);
        Status s = this.getStatus();
        if (s != null) summary.put("status", s);
        i = this.getSubscriptionId();
        if (i != null) summary.put("subscription_id", i);
        String str = this.getBoletoBarcode();
        if (str != null) summary.put("boleto_barcode", str);
        str = this.getBoletoUrl();
        if (str != null) summary.put("boleto_url", str);
        Map<String, String> m = this.getMetadata();
        if (m != null && m.size() > 0) summary.put("metadata", m);
        return summary;
    }

    @Override
    public String toString() {
        return "TransactionResponse{" +
                super.toString() +
                ", object=" + object +
                ", status=" + status +
                ", refuseReason='" + refuseReason + '\'' +
                ", statusReason=" + statusReason +
                ", acquirerName=" + acquirerName +
                ", acquirerId=" + acquirerId +
                ", acquirerResponseCode='" + acquirerResponseCode + '\'' +
                ", authorizationCode='" + authorizationCode + '\'' +
                ", tid='" + tid + '\'' +
                ", nsu='" + nsu + '\'' +
                ", dateCreated=" + dateCreated +
                ", updatedAt=" + updatedAt +
                ", authorizedAmount=" + authorizedAmount +
                ", paidAmount=" + paidAmount +
                ", refundedAmount=" + refundedAmount +
                ", cost=" + cost +
                ", cardLastDigits='" + cardLastDigits + '\'' +
                ", cardFirstDigits='" + cardFirstDigits + '\'' +
                ", cardBrand='" + cardBrand + '\'' +
                ", cardPinMode='" + cardPinMode + '\'' +
                ", antifraudScore='" + antifraudScore + '\'' +
                ", boletoUrl='" + boletoUrl + '\'' +
                ", boletoBarcode='" + boletoBarcode + '\'' +
                ", referer='" + referer + '\'' +
                ", subscriptionId=" + subscriptionId +
//                ", antifraudMetadata=" + antifraudMetadata +
                ", session='" + session + '\'' +
                ", session='" + session + '\'' +
                ", oldStatus=" + oldStatus +
                ", currentStatus=" + currentStatus +
                ", desiredStatus=" + desiredStatus +
                '}';
    }
}
