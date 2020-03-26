package clightning.apis;

import clightning.apis.annotations.ImplFor;
import clightning.apis.annotations.ParamTag;
import clightning.apis.optional.AutoCleanInvoiceParams;
import clightning.apis.optional.FundChannelParams;
import clightning.apis.optional.PayParams;
import clightning.apis.response.*;

public interface BasedPlugin {
    /**
     * https://lightning.readthedocs.io/lightning-autocleaninvoice.7.html
     * <p>
     * autocleaninvoice [cycle_seconds] [expired_by]
     * Set up autoclean of expired invoices.
     */
    String autoCleanInvoice();

    String autoCleanInvoice(AutoCleanInvoiceParams optionalParams);

    /**
     * fundchannel id amount [feerate] [announce] [minconf] [utxos]
     * Fund channel with {id} using {satoshi} (or 'all'), at optional {feerate}. Only use outputs that have {minconf} confirmations.
     * <p>
     * feerate:
     * - urgent: for next block
     * - normal: next four block or so
     * - slow: next 100 blocks or so
     * - <number>[perkw|perkb]
     * // perkw: satoshi-per-kilosipa (weight)
     * // perkb(the default suffix): satoshi-per-kilobyte
     * <p>
     * announce: whether to announce this channel or not, default to true
     * <p>
     * minconf: the minimum number of confirmations that used outputs should have, default to 1
     * <p>
     * utxos: specifies the utxos to be used to fund the channel, as an array of "txid:vout".
     */
    FundChannel fundChannel(String id, long amountSato);

    FundChannel fundChannel(String id, @ParamTag(alias = "amount") long amountSato, FundChannelParams params);

    /**
     * listpays [bolt11]
     * List result of payment {bolt11}, or all
     */
    PayInfo[] listPays();

    PayInfo listPays(@ParamTag(optional = true) String bolt11);

    /**
     * https://lightning.readthedocs.io/lightning-pay.7.html
     * <p>
     * pay bolt11 [msatoshi] [label] [riskfactor] [maxfeepercent] [retry_for] [maxdelay] [exemptfee]
     * Send payment specified by {bolt11} with {amount}
     * <p>
     * riskfactor: default to 10, https://lightning.readthedocs.io/lightning-getroute.7.html
     * <p>
     * maxfeepercent: limits the money paid in fees, and defaults to 0.5
     * <p>
     * retry_for: Until retry_for seconds passes (default: 60), the command will keep finding
     * routes and retrying the payment.
     * <p>
     * maxdelay: a payment may be delayed for up to maxdelay blocks by another node
     * <p>
     * exemptfee: The `exemptfee option can be used for tiny payments which would be dominated by
     */
    PayResult pay(String bolt11);

    PayResult pay(String bolt11, PayParams optionalParams);

    /**
     * paystatus [bolt11]
     * Detail status of attempts to pay {bolt11}, or all
     */
    PayStatus[] payStatus();

    PayStatus payStatus(@ParamTag(optional = true) String bolt11);

    /**
     * plugin subcommand=start|stop|startdir|rescan|list
     * Control plugins (start, stop, startdir, rescan, list)
     */
    @ImplFor("plugin subcommand=start|stop|startdir|rescan|list")
    PluginStatus plugin(PluginCommand command);
}
