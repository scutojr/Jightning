package clightning.apis.response;

import clightning.apis.InvoiceStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Optional;

@Data
public class DetailedInvoice {
    private String label;
    private String bolt11;

    @JsonProperty("payment_hash")
    private String paymentHash;

    private long msatoshi;

    @JsonProperty("amount_msat")
    private String amountMsat;

    private InvoiceStatus status;


    // --------------- only available the invoice is paied --------------------
    @JsonProperty("pay_index")
    private Optional<Long> payIndex;

    @JsonProperty("msatoshi_received")
    private Optional<Long> msatoshiReceived;

    @JsonProperty("amount_received_msat")
    private Optional<String> amountReceivedMsat;

    @JsonProperty("paid_at")
    private Optional<Long> paidAt;
    // ------------------------------------------------------------------------


    private Optional<String> description;

    @JsonProperty("expires_at")
    private long expiresAt;
}
