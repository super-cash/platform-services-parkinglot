package cash.super_.platform.model.supercash.methods.crypto;

import cash.super_.platform.model.supercash.Payment;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity(name = "payment_crypto")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PaymentCrypto extends Payment {

}
