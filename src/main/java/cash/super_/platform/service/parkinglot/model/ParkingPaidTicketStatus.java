package cash.super_.platform.service.parkinglot.model;

import cash.super_.platform.client.parkingplus.model.PagamentoEfetuado;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ParkingPaidTicketStatus {

    @JsonProperty("cnpjGaragem")
    private String cnpjGaragem;

    @JsonProperty("codigoAutorizacao")
    private String codigoAutorizacao;

    @JsonProperty("cpfCnpj")
    private String cpfCnpj;

    @JsonProperty("data")
    private Long data;

    @JsonProperty("estaticonamento")
    private String estaticonamento;

    @JsonProperty("linkLogoGaragem")
    private String linkLogoGaragem;

    @JsonProperty("nfseCodigoVerificacao")
    private String nfseCodigoVerificacao;

    @JsonProperty("nfseNumero")
    private String nfseNumero;

    @JsonProperty("nfseQrCode")
    private String nfseQrCode;

    @JsonProperty("nsu")
    private String nsu;

    @JsonProperty("permanencia")
    private Long permanencia;

    @JsonProperty("permanenciaFim")
    private Long permanenciaFim;

    @JsonProperty("prepago")
    private Boolean prepago;

    @JsonProperty("rps")
    private String rps;

    @JsonProperty("serieRps")
    private String serieRps;

    @JsonProperty("ticket")
    private String ticket;

    @JsonProperty("service_fee")
    private Long serviceFee;

    public ParkingPaidTicketStatus(PagamentoEfetuado pagamentoEfetuado) {
        cnpjGaragem = pagamentoEfetuado.getCnpjGaragem();
        codigoAutorizacao = pagamentoEfetuado.getCodigoAutorizacao();
        cpfCnpj = pagamentoEfetuado.getCpfCnpj();
        data = pagamentoEfetuado.getData();
        estaticonamento = pagamentoEfetuado.getEstaticonamento();
        linkLogoGaragem = pagamentoEfetuado.getLinkLogoGaragem();
        nfseCodigoVerificacao = pagamentoEfetuado.getNfseCodigoVerificacao();
        nfseNumero = pagamentoEfetuado.getNfseNumero();
        nfseQrCode = pagamentoEfetuado.getNfseQrCode();
        nsu = pagamentoEfetuado.getNsu();
        permanencia = pagamentoEfetuado.getPermanencia();
        permanenciaFim = pagamentoEfetuado.getPermanenciaFim();
        prepago = pagamentoEfetuado.isPrepago();
        rps = pagamentoEfetuado.getRps();
        serieRps = pagamentoEfetuado.getSerieRps();
        ticket = pagamentoEfetuado.getTicket();
        serviceFee = Long.valueOf(0);
    }

    public void setServiceFee(Long serviceFee) {
        this.serviceFee = serviceFee;
    }

    public Long getServiceFee() {
        return this.serviceFee;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "cnpjGaragem='" + cnpjGaragem + '\'' +
                ", codigoAutorizacao='" + codigoAutorizacao + '\'' +
                ", cpfCnpj='" + cpfCnpj + '\'' +
                ", data=" + data +
                ", estaticonamento='" + estaticonamento + '\'' +
                ", linkLogoGaragem='" + linkLogoGaragem + '\'' +
                ", nfseCodigoVerificacao='" + nfseCodigoVerificacao + '\'' +
                ", nfseNumero='" + nfseNumero + '\'' +
                ", nfseQrCode='" + nfseQrCode + '\'' +
                ", nsu='" + nsu + '\'' +
                ", permanencia=" + permanencia +
                ", permanenciaFim=" + permanenciaFim +
                ", prepago=" + prepago +
                ", rps='" + rps + '\'' +
                ", serieRps='" + serieRps + '\'' +
                ", ticket='" + ticket + '\'' +
                ", serviceFee=" + serviceFee +
                '}';
    }
}
