package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity(name = "payment_card_holder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @JsonProperty(required = true)
    private String name;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "card_id", nullable = false, foreignKey = @ForeignKey(name = "payment_card_holder_card_fk"))
    private CardResponse card;

    public CardHolder() { }

    public CardHolder(String name) { this.name = name; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CardResponse getCard() {
        return card;
    }

    public void setCard(CardResponse card) {
        this.card = card;
    }

    @Override
    public String toString() {
        return "Holder{" +
                "name='" + name + '\'' +
                '}';
    }
}
