package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity(name = "payment_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @JsonProperty(value = "reference_id", required = true)
    private String referenceId;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private Integer quantity;

    @JsonProperty(value = "unit_amount", required = true)
    private Long unitAmount;

    private Integer weight;

    @Embedded
    private OrderItemDimension dimension;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_id", nullable = false, foreignKey = @ForeignKey(name = "payment_order_payment_item_fk"))
    private PaymentOrder payment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getUnitAmount() {
        return unitAmount;
    }

    public void setUnitAmount(Long unitAmount) {
        this.unitAmount = unitAmount;
    }

    public OrderItemDimension getDimension() {
        return dimension;
    }

    public void setDimension(OrderItemDimension dimension) {
        this.dimension = dimension;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public PaymentOrder getPayment() {
        return payment;
    }

    public void setPayment(PaymentOrder payment) {
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "Item{" +
                "referenceId='" + referenceId + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", unitAmount=" + unitAmount +
                ", dimension=" + dimension +
                ", weight=" + weight +
                '}';
    }
}
