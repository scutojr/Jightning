package clightning.apis;

import clightning.apis.optional.TxPrepareParams;
import clightning.apis.optional.WithdrawParams;
import clightning.apis.response.*;

import java.io.IOException;

public interface BasedBitcoin {
    /**
     * feerates style
     * Return feerate estimates, either satoshi-per-kw ({style} perkw) or satoshi-per-kb ({style} perkb).
     */
    FeeRate feeRates();

    /**
     * newaddr [addresstype]
     * Get a new {bech32, p2sh-segwit} (or all) address to fund a channel (default is bech32)
     */
    LightningAddress newAddr();

    String newBench32Addr();

    String newP2shSegwitAddr();

    /**
     * txdiscard txid
     * Abandon a transaction created by txprepare
     * <p>
     * lightning-cli  txdiscard 6a2590e344155722db01f295591db34e26cf1a0b6a5accbdf84c29cc9771e73a
     */
    TxDiscardResult txDiscard(String txId);

    /**
     * https://lightning.readthedocs.io/lightning-txprepare.7.html
     * <p>
     * txprepare outputs [feerate] [minconf] [utxos]
     * <p>
     * Create a transaction, with option to spend in future (either txsend and txdiscard)
     * <p>
     * feerate is an optional feerate to use
     * <p>
     * minconf specifies the minimum number of confirmations that used outputs should have. Default is 1.
     * <p>
     * utxos specifies the utxos to be used to fund the transaction, as an array of "txid:vout"
     */
    TxPrepareResult txPrepare(Output[] outputs);

    TxPrepareResult txPrepare(Output[] outputs, TxPrepareParams optionalParams);

    /**
     * https://lightning.readthedocs.io/lightning-txsend.7.html
     * <p>
     * txsend txid
     * Sign and broadcast a transaction created by txprepare
     * <p>
     * lightning-cli txsend 07763638d014ebe47e07ba0623c645021414c2867811d80e762207360f94bfb5
     */

    TxSendResult txSend(String txId);

    /**
     * https://lightning.readthedocs.io/lightning-withdraw.7.html
     * <p>
     * sends funds from c-lightning's internal wallet to the address specified in destination.
     * <p>
     * withdraw destination satoshi [feerate] [minconf] [utxos]
     * Send to {destination} address {satoshi} (or 'all') amount via Bitcoin transaction, at optional {feerate}
     * <p>
     * lightning-cli withdraw 2NCpXca1wqQw4AZAEU7rdtg5bYRjDfp5sPZ  10000000
     **/
    WithdrawResutlt withDraw(String destination);

    WithdrawResutlt withDraw(String destination, WithdrawParams optionalParams);

    WithdrawResutlt withDraw(String destination, long satoshi);

    WithdrawResutlt withDraw(String destination, long satoshi, WithdrawParams optionalParams);
}
