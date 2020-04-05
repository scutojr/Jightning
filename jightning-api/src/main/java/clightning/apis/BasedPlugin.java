package clightning.apis;

import clightning.apis.annotations.ImplFor;
import clightning.apis.annotations.ParamTag;
import clightning.apis.optional.AutoCleanInvoiceParams;
import clightning.apis.optional.FundChannelParams;
import clightning.apis.optional.PayParams;
import clightning.apis.response.*;

public interface BasedPlugin {
    String autoCleanInvoice();

    /**
     * Set up automatic cleaning of expired invoices
     *
     * @param optionalParams extra optional parameters
     * @return on success, an empty object is returned
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-autocleaninvoice.7.md>lightning-autocleaninvoice</a>
     */
    String autoCleanInvoice(AutoCleanInvoiceParams optionalParams);

    /**
     * Open a payment channel with a peer by committing a funding transaction to the blockchain as defined in BOLT
     * #2.fundchannel by itself does not attempt to open a connection. A connection must first be established using
     * connect. Once the transaction is confirmed, normal channel operations may begin. Readiness is indicated by
     * listpeers reporting a state of CHANNELD_NORMAL for the channel.
     *
     * @param id         the peer id obtained from {@link BasedNetwork#connect}
     * @param amountSato the amount in satoshis taken from the internal wallet to fund the channel
     * @return {@code FundChannel}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-fundchannel.7.md>lightning-fundchannel</a>
     */
    FundChannel fundChannel(String id, long amountSato);

    /**
     * Open a payment channel with a peer by committing a funding transaction to the blockchain as defined in BOLT
     * #2.fundchannel by itself does not attempt to open a connection. A connection must first be established using
     * connect. Once the transaction is confirmed, normal channel operations may begin. Readiness is indicated by
     * listpeers reporting a state of CHANNELD_NORMAL for the channel.
     *
     * @param id         the peer id obtained from {@link BasedNetwork#connect}
     * @param amountSato the amount in satoshis taken from the internal wallet to fund the channel
     * @param params     extra optional parameters
     * @return {@code FundChannel}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-fundchannel.7.md>lightning-fundchannel</a>
     */
    FundChannel fundChannel(String id, @ParamTag(alias = "amount") long amountSato, FundChannelParams params);

    /**
     * Get the status of all pay commands, or a single one if bolt11 is specified
     *
     * @return {@code PayInfo}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-listpays.7.md>lightning-listpays</a>
     */
    PayInfo[] listPays();

    /**
     * Get the status of all pay commands, or a single one if bolt11 is specified
     *
     * @param bolt11 used to search pays whose bolt11 field matches exactly {@code bolt11}
     * @return {@code PayInfo}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-listpays.7.md>lightning-listpays</a>
     */
    PayInfo listPays(@ParamTag(optional = true) String bolt11);

    /**
     * Attempt to find a route to the given destination, and send the funds it asks for. If the bolt11 does not
     * contain an amount, msatoshi is required, otherwise if it is specified it must be null. msatoshi is in
     * millisatoshi precision; it can be a whole number, or a whole number with suffix msat or sat, or a three
     * decimal point number with suffix sat, or an 1 to 11 decimal point number suffixed by btc.
     *
     * @param bolt11 bolt11 related to the pay
     * @return {@code PayResult}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-pay.7.md>lightning-pay</a>
     */
    PayResult pay(String bolt11);

    /**
     * Attempt to find a route to the given destination, and send the funds it asks for. If the bolt11 does not
     * contain an amount, msatoshi is required, otherwise if it is specified it must be null. msatoshi is in
     * millisatoshi precision; it can be a whole number, or a whole number with suffix msat or sat, or a three
     * decimal point number with suffix sat, or an 1 to 11 decimal point number suffixed by btc.
     *
     * @param bolt11         bolt11 related to the pay
     * @param optionalParams extra optional parameters
     * @return {@code PayResult}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-pay.7.md>lightning-pay</a>
     */
    PayResult pay(String bolt11, PayParams optionalParams);

    /**
     * Detail status of all pay
     *
     * @return {@code PayStatus}
     */
    PayStatus[] payStatus();

    /**
     * Detail status of attempts to pay {bolt11}, or all
     *
     * @param bolt11 bolt11 related to the pay
     * @return {@code PayStatus}
     */
    PayStatus payStatus(@ParamTag(optional = true) String bolt11);

    /**
     * Allow to manage plugins without having to restart lightningd. It takes 1 to 3 parameters: a command
     * (start/stop/startdir/rescan/list) which describes the action to take and optionally one or two parameters
     * which describes the plugin on which the action has to be taken.
     *
     * @param command command to manage the plugin
     * @return {@code PluginStatus}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-plugin.7.md>lightning-plugin</a>
     */
    @ImplFor("plugin subcommand=start|stop|startdir|rescan|list")
    PluginStatus plugin(PluginCommand command);
}
