package cash.super_.platform.model.payment.pagarme;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "pagarme_notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    @Column()
    private Long notificationId;

    private String object;

    private String fingerprint;

    private String event;

    @JsonProperty("old_status")
    private Transaction.Status oldStatus;

    @JsonProperty("desired_status")
    private Transaction.Status desiredStatus;

    @JsonProperty("current_status")
    private Transaction.Status currentStatus;

    private Long datetime;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="transaction_id", nullable = false)
    private Transaction transaction;

    @Transient
    private String uuid;

    @Transient
    private Long marketplaceId;

    @Transient
    private Long storeId;

    @Transient
    private Long userId;

    public Notification() { }

    public Notification(String object, String fingerprint, String event, String oldStatus, String desiredStatus,
                        String currentStatus, String uuid, Long marketplaceId, Long storeId, Long userId) {
        this.object = object;
        this.fingerprint = fingerprint;
        this.event = event;
        this.oldStatus = Transaction.Status.valueOf(oldStatus.toUpperCase());
        this.desiredStatus = Transaction.Status.valueOf(desiredStatus.toUpperCase());
        this.currentStatus = Transaction.Status.valueOf(currentStatus.toUpperCase());
        this.datetime = Instant.now().toEpochMilli();
        this.uuid = uuid;
        this.marketplaceId = marketplaceId;
        this.storeId = storeId;
        this.userId = userId;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Transaction.Status getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(Transaction.Status oldStatus) {
        this.oldStatus = oldStatus;
    }

    public Transaction.Status getDesiredStatus() {
        return desiredStatus;
    }

    public void setDesiredStatus(Transaction.Status desiredStatus) {
        this.desiredStatus = desiredStatus;
    }

    public Transaction.Status getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Transaction.Status currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Long getDatetime() {
        return datetime;
    }

    public void setDatetime(Long datetime) {
        this.datetime = datetime;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getMarketplaceId() {
        return marketplaceId;
    }

    public void setMarketplaceId(Long marketplaceId) {
        this.marketplaceId = marketplaceId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", object='" + object + '\'' +
                ", fingerprint='" + fingerprint + '\'' +
                ", event='" + event + '\'' +
                ", oldStatus=" + oldStatus +
                ", desiredStatus=" + desiredStatus +
                ", currentStatus=" + currentStatus +
                ", datetime=" + datetime +
                ", transaction=" + transaction +
                ", uuid='" + uuid + '\'' +
                ", marketplaceId=" + marketplaceId +
                ", storeId=" + storeId +
                ", userId=" + userId +
                '}';
    }
}
