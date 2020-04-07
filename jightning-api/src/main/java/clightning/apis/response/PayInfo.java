package clightning.apis.response;

import clightning.apis.BasedPlugin;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Optional;

/**
 * Response of {@link BasedPlugin#listPays}
 */
@Data
public class PayInfo {
    private Optional<String> bolt11;

    @JsonProperty("payment_hash")
    private Optional<String> paymentHash;

    private Optional<String> destination;

    @JsonProperty("amount_msat")
    private Optional<String> amountMsat;

    private String status;

    @JsonProperty("payment_preimage")
    private String paymentPreimage;

    @JsonProperty("amount_sent_msat")
    private String amountSentMsat;
}
