package cash.super_.platform.service.parkingplus.payment;

import me.pagar.model.Transaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pagarmeApiClient", url = "${cash.super.platform.service.pagarme.url}")
public interface PagarmeClientService {

    @PostMapping("/transactions/credit")
    public Transaction requestPayment(@RequestBody Transaction transaction);
}