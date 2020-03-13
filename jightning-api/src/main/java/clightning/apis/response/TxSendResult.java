package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// TODO: merge common implementation as one
@Data
public class TxSendResult {
    private String tx; // TODO: decode it into transaction instance

    @JsonProperty("txid")
    private String txId;
}
