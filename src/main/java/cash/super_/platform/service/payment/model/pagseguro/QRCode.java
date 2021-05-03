package cash.super_.platform.service.payment.model.pagseguro;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QRCode {

    private String id;

    private Amount amount;

    private String text;

    private List<Link> links;
}
