package clightning.apis.response;

import clightning.apis.InvoiceStatus;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.Optional;

@Data
public class DetailedInvoice {
    private String label;
    private String bolt11;

    @JsonSetter("payment_hash")
    private String paymentHash;

    private long msatoshi;

    @JsonSetter("amount_msat")
    private String amountMsat;

    private InvoiceStatus status;


    // --------------- only available the invoice is paied --------------------
    @JsonSetter("pay_index")
    private Optional<Long> payIndex;

    @JsonSetter("msatoshi_received")
    private Optional<Long> msatoshiReceived;

    @JsonSetter("amount_received_msat")
    private Optional<String> amountReceivedMsat;

    @JsonSetter("paid_at")
    private Optional<Long> paidAt;
    // ------------------------------------------------------------------------


    private Optional<String> description;

    @JsonSetter("expires_at")
    private long expiresAt;
}
