package cash.super_.platform.service.payment.model.supercash;

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
        return "Dimensions{" +
                "length=" + length +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
