package clightning.apis.optional;

public class FundChannelStartParams extends OptionalParams {
    public FundChannelStartParams setFeeRate(FeeRate feeRate) {
        params.put("feerate", feeRate.toString());
        return this;
    }

    public FundChannelStartParams setAnnounce(FeeRate feeRate) {
        params.put("announce", feeRate.toString());
        return this;
    }

    public FundChannelStartParams setCloseTo(String toAddr) {
        params.put("close_to", toAddr);
        return this;
    }
}
