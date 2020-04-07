package clightning.apis.optional;

/**
 * Extra optional parameters for {@link clightning.apis.BasedPayment#listSendPays(ListSendPaysParams)}
 */
public class ListSendPaysParams extends OptionalParams {
    public ListSendPaysParams setBolt11(String bolt11) {
        params.put("bolt11", bolt11);
        return this;
    }

    public ListSendPaysParams setPaymentHash(String paymentHash) {
        params.put("payment_hash", paymentHash);
        return this;
    }
}
