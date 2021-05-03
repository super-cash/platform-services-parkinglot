package cash.super_.platform.service.payment.model.pagseguro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Order {

    @Length(max = 64)
    private String id;

    @JsonProperty(value = "reference_id")
    private String referenceId;

    @Transient
    private Customer customer;

    private List<Item> items = new ArrayList<>();

    private Shipping shipping;

    @JsonProperty(value = "notification_urls")
    private List<String> notificationUrls;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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

    public void clearItems() {
        this.items.clear();
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    public List<String> getNotificationUrls() {
        return notificationUrls;
    }

    public void setNotificationUrls(List<String> notificationUrls) {
        this.notificationUrls = notificationUrls;
    }

    public void addNotificationUrl(String url) {
        this.notificationUrls.add(url);
    }

    public void clearNotificationUrls() {
        this.notificationUrls = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", referenceId='" + referenceId + '\'' +
                ", customer=" + customer +
                ", items=" + items +
                ", shipping=" + shipping +
                ", notificationUrls=" + notificationUrls +
                '}';
    }
}
