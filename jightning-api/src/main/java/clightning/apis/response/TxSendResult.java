package clightning.apis.response;

import clightning.utils.BitcoinUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;

@Data
public class TxSendResult {
    private String tx;

    @JsonProperty("txid")
    private String txId;

    public Transaction decode(NetworkParameters networkParameters) {
        return BitcoinUtil.decodeTransaction(networkParameters, tx);
    }
}
