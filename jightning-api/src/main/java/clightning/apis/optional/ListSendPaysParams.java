package clightning.apis.optional;

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
