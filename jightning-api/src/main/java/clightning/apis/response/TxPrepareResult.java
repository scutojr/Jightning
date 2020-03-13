package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TxPrepareResult {
    @JsonProperty("unsigned_tx")
    private String unsignedTx;

    @JsonProperty("txid")
    private String txId;
}
