package cash.super_.platform.model.payment.pagarme;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;

public class Item {

    @JsonIgnore
    private Long itemId;

    private String id;

    private String title;

    @JsonProperty("unit_price")
    private Long unitPrice;

    private Integer quantity;

    // TODO: investigate why this is not being properly stored in the database
    private Boolean tangible;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="transaction_id", nullable = false)
    private Transaction transaction;

    public Long getItemId() { return itemId; }

    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Long unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getTangible() {
        return tangible;
    }

    public void setTangible(Boolean tangible) {
        this.tangible = tangible;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                ", tangible=" + tangible +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return getItemId().equals(item.getItemId()) && getId().equals(item.getId()) &&
                getTitle().equals(item.getTitle()) && getUnitPrice().equals(item.getUnitPrice()) &&
                getQuantity().equals(item.getQuantity()) && getTangible().equals(item.getTangible());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItemId(), getId());
    }
}
