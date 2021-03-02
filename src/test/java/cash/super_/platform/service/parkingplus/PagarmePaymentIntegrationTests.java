package cash.super_.platform.service.parkingplus;

import cash.super_.platform.service.parkingplus.autoconfig.ParkingPlusProperties;
import cash.super_.platform.service.parkingplus.payment.PagarmeClientService;
import cash.super_.platform.service.parkingplus.payment.PagarmePaymentProcessorService;
import cash.super_.platform.service.parkingplus.sales.ParkingPlusSalesController;
import me.pagar.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Requires network connectivity as it will call Google's API.
 *
 * @author marcellodesales
 *
 */
@SpringBootTest(properties = {},
    webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("Pagarme Payment in ParkingLot Integration Tests")
@ActiveProfiles({"integration_tests"})
public class PagarmePaymentIntegrationTests {

  @LocalServerPort
  int randomServerPort;

  @Autowired
  private PagarmeClientService pagarmeClientService;

  @Test
  @DisplayName("Test Get Making Payment with Pagarme Success")
  @Disabled
  public void testMakeTransaction() throws Exception {
    Transaction transaction = new Transaction();

    Customer customer = new Customer();
    customer.setType(Customer.Type.INDIVIDUAL);
    customer.setExternalId("testId-0001");
    customer.setName("Leandro");
    customer.setBirthday("1981-12-08");
    customer.setEmail("test@super.cash");
    customer.setCountry("br");

    Collection<Document> documents = new ArrayList();
    Document document = new Document();
    document.setType(Document.Type.CPF);
    document.setNumber("03817304412");
    documents.add(document);
    customer.setDocuments(documents);

    Collection<String> phones = new ArrayList();
    phones.add("+5511982657575");
    customer.setPhoneNumbers(phones);

    Billing billing = new Billing();
    billing.setName("Leandro");
    Address address  = new Address();
    address.setCity("Maceio");
    address.setCountry("br");
    address.setState("sp");
    address.setNeighborhood("Parque Miami");
    address.setStreet("Rua Rio Jari");
    address.setZipcode("09133180");
    address.setStreetNumber("7");
    billing.setAddress(address);

    Collection<Item> items = new ArrayList<>();
    Item item = new Item();
    item.setId("2312312323");
    item.setQuantity(1);
    item.setTangible(Boolean.FALSE);
    item.setTitle("Estacionamento Maceió Shopping");
    item.setUnitPrice(10000);

    transaction.setBilling(billing);
    transaction.setItems(items);
    transaction.setPaymentMethod(Transaction.PaymentMethod.CREDIT_CARD);
    transaction.setAmount(10000);
    transaction.setCardHolderName("Leandro");
    transaction.setCardNumber("1234123412341234");
    transaction.setCardCvv("123");
    transaction.setCardExpirationDate("1121");
    transaction.setCustomer(customer);

    transaction.setCapture(true);
    transaction.setAsync(false);

    Transaction responseTransaction = pagarmeClientService.requestPayment(transaction);
    Assertions.assertThat(responseTransaction).isNotNull();
    Assertions.assertThat(responseTransaction.getCurrentStatus()).isNotNull();
    Assertions.assertThat(responseTransaction.getCurrentStatus()).isEqualTo(Transaction.Status.PAID);
  }

}
