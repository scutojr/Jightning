package clightning.apis;

import clightning.apis.optional.TxPrepareParams;
import clightning.apis.optional.WithdrawParams;
import clightning.apis.response.*;

import java.io.IOException;

public interface Bitcoin {
    /**
     * feerates style
     * Return feerate estimates, either satoshi-per-kw ({style} perkw) or satoshi-per-kb ({style} perkb).
     */
    FeeRate feeRates() throws IOException;

    /**
     * newaddr [addresstype]
     * Get a new {bech32, p2sh-segwit} (or all) address to fund a channel (default is bech32)
     */
    LightningAddress newAddr() throws IOException;

    String newBench32Addr() throws IOException;

    String newP2shSegwitAddr() throws IOException;

    /**
     * txdiscard txid
     * Abandon a transaction created by txprepare
     *
     * lightning-cli  txdiscard 6a2590e344155722db01f295591db34e26cf1a0b6a5accbdf84c29cc9771e73a
     */
    TxDiscardResult txDiscard(String txId) throws IOException;

    /**
     * https://lightning.readthedocs.io/lightning-txprepare.7.html
     *
     * txprepare outputs [feerate] [minconf] [utxos]
     *
     * Create a transaction, with option to spend in future (either txsend and txdiscard)
     *
     * feerate is an optional feerate to use
     *
     * minconf specifies the minimum number of confirmations that used outputs should have. Default is 1.
     *
     * utxos specifies the utxos to be used to fund the transaction, as an array of “txid:vout”
     */
    TxPrepareResult txPrepare(Output[] outputs) throws IOException;

    TxPrepareResult txPrepare(Output[] outputs, TxPrepareParams optionalParams) throws IOException;

    /**
     * https://lightning.readthedocs.io/lightning-txsend.7.html
     *
     * txsend txid
     * Sign and broadcast a transaction created by txprepare
     *
     * lightning-cli txsend 07763638d014ebe47e07ba0623c645021414c2867811d80e762207360f94bfb5
     */

    TxSendResult txSend(String txId) throws IOException;

    /**
     *
     * https://lightning.readthedocs.io/lightning-withdraw.7.html
     *
     * sends funds from c-lightning’s internal wallet to the address specified in destination.
     *
     * withdraw destination satoshi [feerate] [minconf] [utxos]
     *     Send to {destination} address {satoshi} (or 'all') amount via Bitcoin transaction, at optional {feerate}
     *
     * lightning-cli withdraw 2NCpXca1wqQw4AZAEU7rdtg5bYRjDfp5sPZ  10000000
     **/
    WithdrawResutlt withDraw(String destination) throws IOException;

    WithdrawResutlt withDraw(String destination, WithdrawParams optionalParams) throws IOException;

    WithdrawResutlt withDraw(String destination, long satoshi) throws IOException;

    WithdrawResutlt withDraw(String destination, long satoshi, WithdrawParams optionalParams) throws IOException;
}
