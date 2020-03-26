package clightning.apis.optional;

import clightning.apis.UTxO;

import java.util.Arrays;
import java.util.stream.Collectors;

public class WithdrawParams extends OptionalParams {
    public WithdrawParams setFeeRate(FeeRate feeRate) {
        params.put("feerate", feeRate);
        return this;
    }

    /**
     * @param minConf minimum confirmation
     * @return
     */
    public WithdrawParams setMinConf(int minConf) {
        params.put("minconf", minConf);
        return this;
    }

    public WithdrawParams setUtxos(UTxO... utxos) {
        params.put("utxos", Arrays.stream(utxos).map(e -> e.toString()).collect(Collectors.toList()));
        return this;
    }
}
