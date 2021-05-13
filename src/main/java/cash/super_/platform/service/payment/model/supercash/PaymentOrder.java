package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "payment_order")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PaymentOrder extends Payment {

    @Length(max = 64)
    private String orderId;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "payment_order_payment_customer_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty(value = "customer")
    private OrderCustomer customer;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "payment", orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<OrderItem> items = new HashSet<>();

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "payment_order_payment_shipping_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty(value = "shipping")
    private OrderShipping shipping;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String paymentId) {
        this.orderId = paymentId;
    }

    public OrderCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(OrderCustomer customer) {
        this.customer = customer;
    }

    public Set<OrderItem> getItems() {
        return items;
    }

    public void setItems(Set<OrderItem> items) {
        this.items = items;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    public void clearItems() {
        this.items.clear();
    }

    public OrderShipping getShipping() {
        return shipping;
    }

    public void setShipping(OrderShipping shipping) {
        this.shipping = shipping;
    }

    @Override
    public String toString() {
        return "PaymentOrder{" +
                "orderId='" + orderId + '\'' +
                ", customer=" + customer +
                ", items=" + items +
                ", shipping=" + shipping +
                "} " + super.toString();
    }
}
