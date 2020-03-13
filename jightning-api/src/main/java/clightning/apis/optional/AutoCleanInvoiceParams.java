package clightning.apis.optional;

public class AutoCleanInvoiceParams extends OptionalParams {
    /**
     *
     * @param cycleSeconds default to 3600, one hour
     * @return
     */
    public AutoCleanInvoiceParams setCycleSeconds(int cycleSeconds) {
        params.put("cycle_seconds", cycleSeconds);
        return this;
    }

    /**
     *
     * @param expiredBy default to 86400, one day
     * @return
     */
    public AutoCleanInvoiceParams setExpiredBy(int expiredBy) {
        params.put("expired_by", expiredBy);
        return this;
    }
}
