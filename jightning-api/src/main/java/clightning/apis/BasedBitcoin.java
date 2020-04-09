package clightning.apis;

import clightning.RemoteException;
import clightning.apis.annotations.ImplFor;
import clightning.apis.optional.TxPrepareParams;
import clightning.apis.optional.WithdrawParams;
import clightning.apis.response.*;

public interface BasedBitcoin {
    /**
     * @return FeeRate estimates, including satoshi-per-kw ({style} perkw) and satoshi-per-kb ({style} perkb).
     */
    @ImplFor("feerates style")
    FeeRate feeRates();

    /**
     * The newAddr RPC command generates a new address which can subsequently be used to fund channels
     * managed by the c-lightning node.The funding transaction needs to be confirmed before funds can
     * be used.
     *
     * @return newly created LightningAddress
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-newaddr.7.md>lightning-newaddr</a>
     */
    @ImplFor("newaddr [addresstype]")
    LightningAddress newAddr();

    /**
     * The newAddr RPC command generates a new address which can subsequently be used to fund channels
     * managed by the c-lightning node.The funding transaction needs to be confirmed before funds can
     * be used.
     *
     * @return a bech32 address string
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-newaddr.7.md>lightning-newaddr</a>
     */
    @ImplFor("newaddr [addresstype]")
    String newBench32Addr();

    /**
     * The newAddr RPC command generates a new address which can subsequently be used to fund channels
     * managed by the c-lightning node.The funding transaction needs to be confirmed before funds can
     * be used.
     *
     * @return a p2sh-segwit address string
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-newaddr.7.md>lightning-newaddr</a>
     */
    @ImplFor("newaddr [addresstype]")
    String newP2shSegwitAddr();

    /**
     * releases inputs which were reserved for use of the txId from {@link #txPrepare}
     *
     * @param txId transaction id
     * @throws RemoteException while there is no matching transaction id
     */
    TxDiscardResult txDiscard(String txId);

    /**
     * creates an unsigned transaction which spends funds from c-lightning's internal
     * wallet to the outputsspecified in outputs.
     *
     * @param outputs array of {@link Output} that include destination and amount.
     *                It supports the any number of outputs
     */
    TxPrepareResult txPrepare(Output[] outputs);

    /**
     * creates an unsigned transaction which spends funds from c-lightning's internal
     * wallet to the outputsspecified in outputs.
     *
     * @param outputs        array of {@link Output} that include destination and amount.
     *                       It supports the any number of outputs
     * @param optionalParams extra optional input parameters
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-txdiscard.7.md>lightning-txdiscard</a>
     */
    TxPrepareResult txPrepare(Output[] outputs, TxPrepareParams optionalParams);

    /**
     * signs and broadcasts a transaction created by {@link #txPrepare}
     *
     * @param txId id of the transaction to be signed and broadcasted
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-txsend.7.md>lightning-txsend</a>
     */
    TxSendResult txSend(String txId);

    /**
     * sends funds from c-lightning's internal wallet to the address specified in destination.
     *
     * @param destination the address where the funds to be sent
     * @return an object with attributes tx and txid
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-withdraw.7.md>lightning-withdraw</a>
     */
    WithdrawResutlt withDraw(String destination);

    /**
     * sends funds from c-lightning's internal wallet to the address specified in destination.
     *
     * @param destination the address where the funds to be sent
     * @param optionalParams extra optional parameters
     * @return an object with attributes tx and txid
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-withdraw.7.md>lightning-withdraw</a>
     */
    WithdrawResutlt withDraw(String destination, WithdrawParams optionalParams);

    /**
     * sends funds from c-lightning's internal wallet to the address specified in destination.
     *
     * @param destination the address where the funds to be sent
     * @param satoshi the amount to be withdrawn from the internal wallet
     * @return an object with attributes tx and txid
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-withdraw.7.md>lightning-withdraw</a>
     */
    WithdrawResutlt withDraw(String destination, long satoshi);

    /**
     * sends funds from c-lightning's internal wallet to the address specified in destination.
     *
     * @param destination the address where the funds to be sent
     * @param satoshi the amount to be withdrawn from the internal wallet
     * @param optionalParams extra optional parameters
     * @return an object with attributes tx and txid
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-withdraw.7.md>lightning-withdraw</a>
     */
    WithdrawResutlt withDraw(String destination, long satoshi, WithdrawParams optionalParams);
}
