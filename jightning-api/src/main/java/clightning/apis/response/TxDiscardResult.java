package clightning.apis.response;

import clightning.utils.BitcoinUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;

/**
 * Response of {@link clightning.apis.BasedBitcoin#txDiscard}
 */
public class TxDiscardResult {
    @JsonProperty("unsigned_tx")
    private String unsignedTx;

    @JsonProperty("txid")
    private String txId;

    public Transaction decode(NetworkParameters networkParameters) {
        return BitcoinUtil.decodeTransaction(networkParameters, unsignedTx);
    }
}
