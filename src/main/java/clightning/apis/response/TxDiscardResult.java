package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;

public class TxDiscardResult {
    @JsonSetter("unsigned_tx")
    private String unsignedTx; // TODO: decode it into transaction instance

    @JsonSetter("txid")
    private String txId;
}
