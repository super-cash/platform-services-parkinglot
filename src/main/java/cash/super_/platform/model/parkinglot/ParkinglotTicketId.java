package cash.super_.platform.model.parkinglot;

import java.io.Serializable;
import java.util.Objects;

/**
 * https://www.baeldung.com/jpa-composite-primary-keys#idclass
 */
public class ParkinglotTicketId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long ticketNumber;

    private Long userId;

    private Long storeId;

    public ParkinglotTicketId() {}

    public ParkinglotTicketId(Long ticketNumber, Long userId, Long storeId) {
        this.ticketNumber = ticketNumber;
        this.userId = userId;
        this.storeId = storeId;
    }

    public Long getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(Long ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkinglotTicketId that = (ParkinglotTicketId) o;
        return Objects.equals(ticketNumber, that.ticketNumber)
                && Objects.equals(userId, that.userId)
                && Objects.equals(storeId, that.storeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketNumber, userId, storeId);
    }
}
