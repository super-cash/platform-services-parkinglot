package cash.super_.platform.service.payment.model.pagseguro;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item {

    @JsonIgnore
    private String itemId;

    @JsonProperty(value = "reference_id", required = true)
    private String referenceId;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private Integer quantity;

    @JsonProperty(value = "unit_amount", required = true)
    private Long unitAmount;

    private Dimensions dimensions;

    private Integer weight;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public Dimensions getDimensions() {
        return dimensions;
    }

    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", referenceId='" + referenceId + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", unitAmount=" + unitAmount +
                ", dimensions=" + dimensions +
                ", weight=" + weight +
                '}';
    }
}
