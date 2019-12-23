package clightning.apis;

import clightning.apis.option.FundChannelParams;
import clightning.apis.response.FundChannel;

import java.io.IOException;

public interface Plugin {
    /**
     *autocleaninvoice [cycle_seconds] [expired_by]
     *    Set up autoclean of expired invoices.
     */
    void autoCleanInvoice();

    /**
     *fundchannel id amount [feerate] [announce] [minconf] [utxos]
     *    Fund channel with {id} using {satoshi} (or 'all'), at optional {feerate}. Only use outputs that have {minconf} confirmations.
     *
     * feerate:
     *     - urgent: for next block
     *     - normal: next four block or so
     *     - slow: next 100 blocks or so
     *     - <number>[perkw|perkb]
     *     // perkw: satoshi-per-kilosipa (weight)
     *     // perkb(the default suffix): satoshi-per-kilobyte
     *
     * announce: whether to announce this channel or not, default to true
     *
     * minconf: the minimum number of confirmations that used outputs should have, default to 1
     *
     * utxos: specifies the utxos to be used to fund the channel, as an array of “txid:vout”.
     */
    FundChannel fundChannel(String id, long amountSato) throws IOException;

    FundChannel fundChannel(String id, long amountSato, FundChannelParams params) throws IOException;

    /**
     *listpays [bolt11]
     *    List result of payment {bolt11}, or all
     */
    void listPays();

    /**
     * https://lightning.readthedocs.io/lightning-pay.7.html
     *
     *pay bolt11 [msatoshi] [label] [riskfactor] [maxfeepercent] [retry_for] [maxdelay] [exemptfee]
     *    Send payment specified by {bolt11} with {amount}
     *
     * riskfactor: default to 10, https://lightning.readthedocs.io/lightning-getroute.7.html
     *
     * maxfeepercent: limits the money paid in fees, and defaults to 0.5
     *
     * retry_for: Until retry_for seconds passes (default: 60), the command will keep finding
     *     routes and retrying the payment.
     *
     * maxdelay: a payment may be delayed for up to maxdelay blocks by another node
     *
     * exemptfee: The `exemptfee option can be used for tiny payments which would be dominated by
     *     the fee leveraged by forwarding nodes. Setting exemptfee allows the maxfeepercent check
     *     to be skipped on fees that are smaller than exemptfee (default: 5000 millisatoshi).
     */
    void pay();

    /**
     *paystatus [bolt11]
     *    Detail status of attempts to pay {bolt11}, or all
     */
    void payStatus();

    /**
     *plugin subcommand=start|stop|startdir|rescan|list
     *    Control plugins (start, stop, startdir, rescan, list)
     */
    void plugin();
}
