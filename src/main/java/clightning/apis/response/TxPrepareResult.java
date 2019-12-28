package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class TxPrepareResult {
    @JsonSetter("unsigned_tx")
    private String unsignedTx;

    @JsonSetter("txid")
    private String txId;
}
