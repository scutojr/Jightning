package clightning.apis.optional;

/**
 * Extra optional parameters for {@link clightning.apis.BasedPayment#invoice(long, String, String, InvoiceParams)}
 */
public class InvoiceParams extends OptionalParams {
    public InvoiceParams setExpiry(int expiry, ExpiryUnit unit) {
        params.put("expiry", expiry + unit.getSign());
        return this;
    }

    public InvoiceParams setExpiry(int expiry) {
        return setExpiry(expiry, ExpiryUnit.second);
    }

    public InvoiceParams setFallbacks(String... fallback) {
        params.put("fallbacks", fallback);
        return this;
    }

    public InvoiceParams setPreimage(String preImage) {
        params.put("preimage", preImage);
        return this;
    }

    /**
     * @param exposePrivateChannels
     * @return
     */
    public InvoiceParams setExposePrivateChannels(boolean exposePrivateChannels) {
        params.put("exposeprivatechannels", exposePrivateChannels);
        return this;
    }
}
