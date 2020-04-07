package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response of {@link clightning.apis.BasedPayment#sendPay}
 */
@Data
public class SendPayResult {
    private String message;
    private String id;

    @JsonProperty("payment_hash")
    private String paymentHash;

    private String destination;
    private long msatoshi;

    @JsonProperty("amount_msat")
    private String amountMsat;

    @JsonProperty("msatoshi_sent")
    private long msatoshiSent;

    @JsonProperty("amount_sent_msat")
    private String amountSentMsat;

    @JsonProperty("created_at")
    private long createdAt;

    private String status;
}
