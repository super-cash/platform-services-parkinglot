package cash.super_.platform.service.payment.model.supercash.types.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Embeddable;

@Embeddable
public class OrderItemDimension {

    @JsonProperty
    private Integer length;

    @JsonProperty
    private Integer width;

    @JsonProperty
    private Integer height;

    public OrderItemDimension() {

    }

    public OrderItemDimension(Integer length, Integer width, Integer height) {
        this.length = length;
        this.width = width;
        this.height = height;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("OrderItemDimension{");
        sb.append(System.lineSeparator());
        sb.append("  \"length\": ").append(length).append(',').append(System.lineSeparator());
        sb.append("  \"width\": ").append(width).append(',').append(System.lineSeparator());
        sb.append("  \"height\": ").append(height).append(',').append(System.lineSeparator());
        sb.append(super.toString()).append(System.lineSeparator()).append('}');
        return sb.toString();
    }
}
