package clightning.apis;

import clightning.apis.annotations.ParamTag;
import clightning.apis.optional.InvoiceParams;
import clightning.apis.optional.ListSendPaysParams;
import clightning.apis.optional.SendPayParams;
import clightning.apis.response.*;

public interface BasedPayment {
    /**
     * Check and parse a bolt11 string as specified by the BOLT 11 specification
     *
     * @param bolt11 bolt11 string
     * @return bolt11 string
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-decodepay.7.md>lightning-decodepay</a>
     */
    Bolt11 decodePay(String bolt11);

    /**
     * Check and parse a bolt11 string as specified by the BOLT 11 specification
     *
     * @param bolt11      bolt11 string
     * @param description description for this call
     * @return bolt11 string
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-decodepay.7.md>lightning-decodepay</a>
     */
    Bolt11 decodePay(String bolt11, @ParamTag(optional = true) String description);

    /**
     * Remove all invoices that have expired on or before the given maxexpirytime
     *
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-delexpiredinvoice.7.md>lightning-delexpiredinvoice</a>
     */
    void delExpiredInvoice();

    /**
     * Remove all invoices that have expired on or before the given maxexpirytime
     *
     * @param maxExpiryTimeSec maxExpiryTime in second
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-delexpiredinvoice.7.md>lightning-delexpiredinvoice</a>
     */
    void delExpiredInvoice(@ParamTag(optional = true, alias = "maxexpirytime") int maxExpiryTimeSec);

    /**
     * Remove an invoice with status as given in {@code listInvoices}.
     * The caller should be particularly aware of the error case caused by the status changing just before
     * this command is invoked!
     *
     * @param label  label of the invoice
     * @param status status of the invoice
     * @return
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-delinvoice.7.md>lightning-delinvoice</a>
     */
    DetailedInvoice delInvoice(String label, InvoiceStatus status);

    /**
     * Create the expectation of a payment of a given amount of milli-satoshi: it returns a unique token which another
     * lightning daemon can use to pay this invoice. This token includes a route hint description of an incoming channel
     * with capacity to pay the invoice, if any exists.
     *
     * @param msatoshi    amount of satoshi in mill precision
     * @param label       label of this invoice
     * @param description a short description of purpose of payment
     * @return object of information about the created invoice
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-invoice.7.md>lightning-invoice</a>
     */
    SimpleInvoice invoice(long msatoshi, String label, String description);

    /**
     * Create the expectation of a payment of a given amount of milli-satoshi: it returns a unique token which another
     * lightning daemon can use to pay this invoice. This token includes a route hint description of an incoming channel
     * with capacity to pay the invoice, if any exists.
     *
     * @param msatoshi       amount of satoshi in mill precision
     * @param label          label of this invoice
     * @param description    a short description of purpose of payment
     * @param optionalParams extra optional parameters
     * @return object of information about the created invoice
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-invoice.7.md>lightning-invoice</a>
     */
    SimpleInvoice invoice(long msatoshi, String label, String description, InvoiceParams optionalParams);

    /**
     * Get the status of a specific invoice, if it exists, or the status of all invoices if given no argument
     *
     * @return an array of {@code DetailedInvoice}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-listinvoices.7.md>lightning-listinvoices</a>
     */
    DetailedInvoice[] listInvoices();

    /**
     * Get the status of a specific invoice, if it exists, or the status of all invoices if given no argument
     *
     * @param label invoice label
     * @return an array of {@code DetailedInvoice}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-listinvoices.7.md>lightning-listinvoices</a>
     */
    DetailedInvoice[] listInvoices(@ParamTag(optional = true) String label);

    /**
     * Get the status of all {@link #sendPay} (which is also used by the {@link BasedPlugin#pay}).
     *
     * @return {@code PayResult}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-listsendpays.7.md>lightning-listsendpays</a>
     */
    PayResult[] listSendPays();

    /**
     * Get the status of all {@link #sendPay} (which is also used by the {@link BasedPlugin#pay}), or with bolt11 or payment_hash
     * limits results to that specific payment. You cannot specify both.
     *
     * @param optionalParams extra optional parameters
     * @return {@code PayResult}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-listsendpays.7.md>lightning-listsendpays</a>
     */
    PayResult[] listSendPays(ListSendPaysParams optionalParams);

    /**
     * List transactions that we stored in the wallet
     *
     * @return an array of {@code Transaction}
     */
    Transaction[] listTransactions();

    /**
     * Attempt to send funds associated with the given payment_hash, along a route to the final destination in the route.
     *
     * @param route       an array of @{code Route} information needed to send the pay to destination peer
     * @param paymentHash payment hash of the pay
     * @return {@code SendPayResult}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-sendpay.7.md>lightning-sendpay</a>
     */
    SendPayResult sendPay(Route[] route, String paymentHash);

    /**
     * Attempt to send funds associated with the given payment_hash, along a route to the final destination in the route.
     *
     * @param route          an array of @{code Route} information needed to send the pay to destination peer
     * @param paymentHash    payment hash of the pay
     * @param optionalParams extra optional parameters
     * @return {@code SendPayResult}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-sendpay.7.md>lightning-sendpay</a>
     */
    SendPayResult sendPay(Route[] route, String paymentHash, SendPayParams optionalParams);

    /**
     * Wait until an invoice is paid, then returns a single entry as per listinvoice. It will not return for any
     * invoices paid prior to or including the lastpay_index.
     *
     * @param lastPayIndex start index of the pay inside the lightning daemon and wait from this index
     * @return {@code DetailedInvoice}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-waitanyinvoice.7.md>lightning-waitanyinvoice</a>
     */
    DetailedInvoice waitAnyInvoice(@ParamTag(optional = true) int lastPayIndex);

    /**
     * Wait until a specific invoice is paid, then returns that single entry as per {@link BasedPayment#listInvoices}
     *
     * @param label labe of the invoice
     * @return
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-waitinvoice.7.md>lightning-waitinvoice</a>
     */
    DetailedInvoice waitInvoice(String label);

    /**
     * Poll or wait for the status of an outgoing payment that was initiated by a previous {@link #sendPay} invocation.
     *
     * @param paymentHash payment hash related to the pay
     * @return {@code PayResult}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-waitsendpay.7.md>lightning-waitsendpay</a>
     */
    PayResult waitSendPay(String paymentHash);

    /**
     * Poll or wait for the status of an outgoing payment that was initiated by a previous {@link #sendPay} invocation.
     *
     * @param paymentHash payment hash related to the pay
     * @param timeout     wait until timeout in second
     * @return {@code PayResult}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-waitsendpay.7.md>lightning-waitsendpay</a>
     */
    PayResult waitSendPay(String paymentHash, @ParamTag(optional = true) int timeout);
}
