package clightning.apis.optional;

import clightning.apis.UTxO;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FundChannelParams extends OptionalParams {

    public FundChannelParams setFeeRate(FeeRate feeRate) {
        params.put("feerate", feeRate.toString());
        return this;
    }

    public FundChannelParams setAnnounce(boolean announce) {
        params.put("announce", announce);
        return this;
    }

    public FundChannelParams setMinConf(int minConf) {
        params.put("minconf", minConf);
        return this;
    }

    /**
     * @param uTxO
     * @return
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
