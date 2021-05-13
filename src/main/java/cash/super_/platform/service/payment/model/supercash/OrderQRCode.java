package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity(name = "payment_qrcode")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderQRCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String qrCodeId;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Amount amount;

    private String text;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "payment_payment_link",
            joinColumns = @JoinColumn(name = "payment_id", foreignKey = @ForeignKey(name =
                    "payment_payment_payment_link_fk")),
            inverseJoinColumns = @JoinColumn(name = "link_id", foreignKey = @ForeignKey(name =
                    "payment_link_payment_payment_link_fk")))
    private List<Link> links;

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    @JsonProperty(value = "qrCodeId")
    public String getQrCodeId() {
        return qrCodeId;
    }

    @JsonProperty(value = "id")
    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "QRCode{" +
                "id=" + id +
                ", qrCodeId='" + qrCodeId + '\'' +
                ", amount=" + amount +
                ", text='" + text + '\'' +
                ", links=" + links +
                '}';
    }
}
