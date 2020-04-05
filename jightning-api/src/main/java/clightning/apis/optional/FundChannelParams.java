package clightning.apis.optional;

import clightning.apis.UTxO;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FundChannelParams extends OptionalParams {

    /**
     * Set fee rate
     *
     * @param feeRate fee rate used to create the channel
     * @return object being called
     */
    public FundChannelParams setFeeRate(FeeRate feeRate) {
        params.put("feerate", feeRate.toString());
        return this;
    }

    /**
     * Set the announce that triggers whether to announce this channel or not
     *
     * @param announce whether to announce this channel or not
     * @return object being called
     */
    public FundChannelParams setAnnounce(boolean announce) {
        params.put("announce", announce);
        return this;
    }

    /**
     * Set the minimum confirmation
     *
     * @param minConf the minimum number of confirmations that used outputs should have.Default is 1.
     * @return object being called
     */
    public FundChannelParams setMinConf(int minConf) {
        params.put("minconf", minConf);
        return this;
    }

    /**
     * Set an array of unspent transaction output
     *
     * @param uTxO specifies the utxos to be used to fund the channel.
     * @return object being called
     */
    public FundChannelParams setUTxOs(UTxO... uTxO) {
        String[] uTxOs = (String[]) Arrays.asList(uTxO)
                .stream()
                .map(t -> t.toString())
                .collect(Collectors.toList())
                .toArray(new String[]{});
        params.put("utxos", uTxOs);
        return this;
    }
}
