package cash.super_.platform.service.payment.model.pagarme;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecipientResponse {

    @JsonProperty(value = "recipient_id")
    private String recipientId;

    @JsonProperty(value = "document_id")
    private String documentId;

    public RecipientResponse(String recipientId, String documentId) {
        this.recipientId = recipientId;
        this.documentId = documentId;
    }
}
