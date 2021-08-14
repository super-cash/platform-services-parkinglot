package cash.super_.platform.model.supercash.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "payment_card")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @JsonProperty(value = "card_id")
    private String cardId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardId() {
        return cardId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(System.lineSeparator());
        sb.append("Card{");
        sb.append(System.lineSeparator());
        sb.append("  \"id\": ").append(id).append(',').append(System.lineSeparator());
        sb.append("  \"cardId\": \"").append(cardId).append("\",").append(System.lineSeparator());
        sb.append(System.lineSeparator()).append('}');
        return sb.toString();
    }
}
