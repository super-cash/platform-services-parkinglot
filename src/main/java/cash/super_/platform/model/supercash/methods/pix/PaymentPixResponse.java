package cash.super_.platform.model.supercash.methods.pix;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Entity;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentPixResponse extends PaymentPix {

}
