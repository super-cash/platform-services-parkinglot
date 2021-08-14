package cash.super_.platform.model.supercash.types.order;

import cash.super_.platform.model.supercash.Payment;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "payment_order")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PaymentOrder extends Payment {

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
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("PaymentOrder{");
        sb.append(System.lineSeparator());
        sb.append("  \"customer\": ").append(customer).append(',').append(System.lineSeparator());
        sb.append("  \"items\": ").append(items).append(',').append(System.lineSeparator());
        sb.append("  \"shipping\": ").append(shipping).append(',').append(System.lineSeparator());
        sb.append(super.toString()).append(System.lineSeparator()).append('}');
        return sb.toString();
    }
}
