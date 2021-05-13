package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity(name = "payment_shipping")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderShipping {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "payment_shipping_payment_address_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty(required = true)
    private OrderAddress address;

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public OrderAddress getAddress() {
        return address;
    }

    public void setAddress(OrderAddress address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "OrderShipping{" +
                "id=" + id +
                ", address=" + address +
                '}';
    }
}
