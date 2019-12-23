package clightning.apis;

public interface Plugin {
    /**
     *autocleaninvoice [cycle_seconds] [expired_by]
     *    Set up autoclean of expired invoices.
     */
    void autoCleanInvoice();

    /**
     *fundchannel id amount [feerate] [announce] [minconf] [utxos]
     *    Fund channel with {id} using {satoshi} (or 'all'), at optional {feerate}. Only use outputs that have {minconf} confirmations.
     */
    void fundChannel();

    /**
     *listpays [bolt11]
     *    List result of payment {bolt11}, or all
     */
    void listPays();

    /**
     *pay bolt11 [msatoshi] [label] [riskfactor] [maxfeepercent] [retry_for] [maxdelay] [exemptfee]
     *    Send payment specified by {bolt11} with {amount}
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
