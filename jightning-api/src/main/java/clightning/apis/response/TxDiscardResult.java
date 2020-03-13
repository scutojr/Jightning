package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TxDiscardResult {
    @JsonProperty("unsigned_tx")
    private String unsignedTx; // TODO: decode it into transaction instance

    @JsonProperty("txid")
    private String txId;
}
