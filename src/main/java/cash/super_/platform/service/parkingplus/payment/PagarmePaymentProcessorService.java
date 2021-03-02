package cash.super_.platform.service.parkingplus.payment;

import brave.Tracer;
import cash.super_.platform.client.parkingplus.model.PagamentoAutorizadoRequest;
import cash.super_.platform.client.parkingplus.model.PagamentoRequest;
import me.pagar.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Proxy service to Retrieve the status of tickets, process payments, etc.
 * 
 * https://demonstracao.parkingplus.com.br/servicos/swagger-ui.html#!/servico-pagamento-ticket-2/pagarTicketAutorizadoUsingPOST
 *
 * @author marcellodesales
 *
 */
@Service
public class PagarmePaymentProcessorService {

  protected static final Logger LOG = LoggerFactory.getLogger(PagarmePaymentProcessorService.class);

  @Autowired
  protected Tracer tracer;

  @Autowired
  private PagarmeClientService pagarmeClientService;

  public Transaction processPayment(String userId, PagamentoRequest payRequest) {
    Transaction transaction = new Transaction();
    LOG.debug("Payment auth request: {}", payRequest);

    Customer customer = new Customer();
    customer.setType(Customer.Type.INDIVIDUAL);
    customer.setExternalId(userId);
    customer.setName(payRequest.getPortador());
    customer.setBirthday("1981-12-08");
    customer.setEmail("test@super.cash");
    customer.setCountry("br");

    Collection<Document> documents = new ArrayList();
    Document document = new Document();
//    if (payRequest.getDadosCpf() != null) {
//      document.setType(Document.Type.CPF);
//
//    } else {
//      document.setType(Document.Type.CNPJ);
//    }
//    document.setNumber(payRequest.getCpfCnpj().toString());
    document.setType(Document.Type.CPF);
    document.setNumber("03817304412");
    documents.add(document);
    customer.setDocuments(documents);

    Collection<String> phones = new ArrayList();
    phones.add("+5511982657575");
    customer.setPhoneNumbers(phones);

    Billing billing = new Billing();
    billing.setName(payRequest.getPortador());
    Address address  = new Address();
    address.setCity("Maceió");
    address.setCountry("br");
    address.setState("sp");
    address.setNeighborhood("Parque Miami");
    address.setStreet("Rua Rio Jari");
    address.setZipcode("09133180");
    address.setStreetNumber("7");
    billing.setAddress(address);

//    Shipping shipping = new Shipping();
//    shipping.setAddress(address);
//    shipping.setName(payRequest.getPortador());
//    shipping.setFee(0);

    Collection<Item> items = new ArrayList<>();
    Item item = new Item();
    item.setId(payRequest.getNumeroTicket());
    item.setQuantity(1);
    item.setTangible(Boolean.FALSE);
    item.setTitle("Estacionamento Maceió Shopping");
    item.setUnitPrice(payRequest.getValor());

//    transaction.setShipping(shipping);
    transaction.setBilling(billing);
    transaction.setItems(items);
    transaction.setPaymentMethod(Transaction.PaymentMethod.CREDIT_CARD);
    transaction.setAmount(payRequest.getValor());
    transaction.setCardHolderName(payRequest.getPortador());
    transaction.setCardNumber(payRequest.getCartaoDeCredito().toString());
    transaction.setCardCvv(payRequest.getCodigoDeSeguranca());
    transaction.setCardExpirationDate(payRequest.getValidade());
    transaction.setCustomer(customer);

    return pagarmeClientService.requestPayment(transaction);
  }

}
