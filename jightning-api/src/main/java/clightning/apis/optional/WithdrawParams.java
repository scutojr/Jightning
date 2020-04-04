package clightning.apis.optional;

import clightning.apis.UTxO;

import java.util.Arrays;
import java.util.stream.Collectors;

public class WithdrawParams extends OptionalParams {
    /**
     * set the fee rate
     *
     * @param feeRate feerate
     * @return object being called
     */
    public WithdrawParams setFeeRate(FeeRate feeRate) {
        params.put("feerate", feeRate);
        return this;
    }

    /**
     * set the minimum confirmation
     *
     * @param minConf minimum confirmation
     * @return object being called
     */
    public WithdrawParams setMinConf(int minConf) {
        params.put("minconf", minConf);
        return this;
    }

    /**
     * set the unspent transaction outputs
     *
     * @param utxos unspent transaction outputs
     * @return object being called
     */
    public WithdrawParams setUtxos(UTxO... utxos) {
        params.put("utxos", Arrays.stream(utxos).map(e -> e.toString()).collect(Collectors.toList()));
        return this;
    }
}
