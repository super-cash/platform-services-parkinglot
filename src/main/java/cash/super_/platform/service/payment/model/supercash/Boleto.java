package cash.super_.platform.service.payment.model.supercash;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Entity;

@Entity(name = "payment_boleto")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Boleto extends Payment {

}
