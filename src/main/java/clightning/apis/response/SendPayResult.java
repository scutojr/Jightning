package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class SendPayResult {
    private String message;
    private String id;

    @JsonSetter("payment_hash")
    private String paymentHash;

    private String destination;
    private long msatoshi;

    @JsonSetter("amount_msat")
    private String amountMsat;

    @JsonSetter("msatoshi_sent")
    private long msatoshiSent;

    @JsonSetter("amount_sent_msat")
    private String amountSentMsat;

    @JsonSetter("created_at")
    private long createdAt;

    private String status;
}
