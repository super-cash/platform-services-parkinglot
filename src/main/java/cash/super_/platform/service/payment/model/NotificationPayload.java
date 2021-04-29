package cash.super_.platform.service.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationPayload {

    private String notificationId;

    private String fingerprint;

    private String event;

    @JsonProperty("old_status")
    private String oldStatus;

    @JsonProperty("desired_status")
    private String desiredStatus;

    @JsonProperty("current_status")
    private String currentStatus;

    private String object;

    private String uuid;

    public NotificationPayload(String notificationId, String fingerprint, String event, String oldStatus,
                               String desiredStatus, String currentStatus, String object, String uuid) {
        this.notificationId = notificationId;
        this.fingerprint = fingerprint;
        this.event = event;
        this.oldStatus = oldStatus;
        this.desiredStatus = desiredStatus;
        this.currentStatus = currentStatus;
        this.object = object;
        this.uuid = uuid;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
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

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getDesiredStatus() {
        return desiredStatus;
    }

    public void setDesiredStatus(String desiredStatus) {
        this.desiredStatus = desiredStatus;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "NotificationPayload{" +
                "notificationId='" + notificationId + '\'' +
                ", fingerprint='" + fingerprint + '\'' +
                ", event='" + event + '\'' +
                ", oldStatus='" + oldStatus + '\'' +
                ", desiredStatus='" + desiredStatus + '\'' +
                ", currentStatus='" + currentStatus + '\'' +
                ", object='" + object + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
