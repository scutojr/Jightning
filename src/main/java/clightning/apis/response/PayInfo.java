package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class PayInfo {
    private String bolt11;
    private String status;

    @JsonSetter("payment_preimage")
    private String paymentPreimage;

    @JsonSetter("amount_sent_msat")
    private String amountSentMsat;
}
