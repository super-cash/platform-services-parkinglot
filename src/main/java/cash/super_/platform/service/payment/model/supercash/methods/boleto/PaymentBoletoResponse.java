package cash.super_.platform.service.payment.model.supercash.methods.boleto;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Entity;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentBoletoResponse extends PaymentBoleto {

}
