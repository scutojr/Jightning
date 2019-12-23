package clightning.apis;

import clightning.apis.response.FeeRate;
import clightning.apis.response.LightningAddress;

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

    String newBench32Addr();

    String newP2shSegwitAddr();

    /**
     * txdiscard txid
     * Abandon a transaction created by txprepare
     */
    void txDiscard();

    /**
     * txprepare outputs [feerate] [minconf] [utxos]
     * Create a transaction, with option to spend in future (either txsend and txdiscard)
     */
    void txPrepare();

    /**
     * txsend txid
     * Sign and broadcast a transaction created by txprepare
     */

    void txSend();
    /**
     * withdraw destination satoshi [feerate] [minconf] [utxos]
     *     Send to {destination} address {satoshi} (or 'all') amount via Bitcoin transaction, at optional {feerate}
     *
     **/
}
