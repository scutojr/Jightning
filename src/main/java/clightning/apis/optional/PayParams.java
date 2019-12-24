package clightning.apis.optional;

/**
 *  riskfactor: default to 10, https://lightning.readthedocs.io/lightning-getroute.7.html
 *
 *  maxfeepercent: limits the money paid in fees, and defaults to 0.5
 *
 *  retry_for: Until retry_for seconds passes (default: 60), the command will keep finding
 *      routes and retrying the payment.
 *
 *  maxdelay: a payment may be delayed for up to maxdelay blocks by another node
 *
 *  exemptfee: The `exemptfee option can be used for tiny payments which would be dominated by
 *      the fee leveraged by forwarding nodes. Setting exemptfee allows the maxfeepercent check
 *      to be skipped on fees that are smaller than exemptfee (default: 5000 millisatoshi).
 * /
 */
public class PayParams extends OptionalParams {
    public PayParams setSatoshi(long msatoshi) {
        params.put("msatoshi", msatoshi);
        return this;
    }

    public PayParams setLabel(String label) {
        params.put("label", label);
        return this;
    }

    public PayParams setRiskFactor(int riskFactor) {
        params.put("riskfactor", riskFactor);
        return this;
    }

    public PayParams setMaxFeePercent(float maxFeePercent) {
        params.put("maxfeepercent", maxFeePercent);
        return this;
    }

    public PayParams setRetryFor(int retryForSecond) {
        params.put("retry_for", retryForSecond);
        return this;
    }

    public PayParams setMaxDelay(int maxDelay) {
        params.put("maxdelay", maxDelay);
        return this;
    }

    public PayParams setExemptFee(long exemptFee) {
        params.put("exemptfee", exemptFee);
        return this;
    }
}
