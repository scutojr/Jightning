package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PayInfo {
    private String bolt11;
    private String status;

    @JsonProperty("payment_preimage")
    private String paymentPreimage;

    @JsonProperty("amount_sent_msat")
    private String amountSentMsat;
}
