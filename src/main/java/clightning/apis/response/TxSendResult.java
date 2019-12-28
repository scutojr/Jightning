package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

// TODO: merge common implementation as one
@Data
public class TxSendResult {
    private String tx; // TODO: decode it into transaction instance

    @JsonSetter("txid")
    private String txId;
}
