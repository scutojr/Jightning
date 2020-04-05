package clightning.apis.optional;

public class FundChannelStartParams extends OptionalParams {
    /**
     * Sets the feerate for subsequent commitment transactions
     *
     * @param feeRate fee rate
     * @return object being called
     */
    public FundChannelStartParams setFeeRate(FeeRate feeRate) {
        params.put("feerate", feeRate.toString());
        return this;
    }

    /**
     * set the announce
     *
     * @param announce indicates whether or not to announce this channel
     * @return object being called
     */
    public FundChannelStartParams setAnnounce(boolean announce) {
        params.put("announce", announce);
        return this;
    }

    /**
     * set the Bitcoin address to which the channel funds should be sent to on close
     *
     * @param toAddr bitcoin address
     * @return object being called
     */
    public FundChannelStartParams setCloseTo(String toAddr) {
        params.put("close_to", toAddr);
        return this;
    }
}
