package clightning.apis.optional;

/**
 * Extra optional parameters for {@link clightning.apis.BasedPlugin#pay(String, PayParams)}
 */
public class PayParams extends OptionalParams {
    public PayParams setMsatoshi(long msatoshi) {
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
