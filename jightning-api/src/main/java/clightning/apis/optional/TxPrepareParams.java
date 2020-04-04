package clightning.apis.optional;

import clightning.apis.UTxO;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TxPrepareParams extends OptionalParams {
    /**
     * set the feerate
     *
     * @param feeRate feerate to use
     * @return the object being called
     */
    public TxPrepareParams setFeeRate(FeeRate feeRate) {
        params.put("feerate", feeRate.toString());
        return this;
    }

    /**
     * set the minimum confirmation
     *
     * @param minConfirmation the minimum number of confirmations that used outputs should have.Default is 1.
     * @return the object being called
     */
    public TxPrepareParams setMinConf(int minConfirmation) {
        params.put("minconf", minConfirmation);
        return this;
    }

    /**
     * set the array of unspent transaction output to be used to fund the transaction
     *
     * @param utxos the array of unspent transaction output to be used to fund the transaction
     * @return the object being called
     */
    public TxPrepareParams setUtxos(UTxO... utxos) {
        String[] uTxOs = (String[]) Arrays.asList(utxos)
                .stream()
                .map(t -> t.toString())
                .collect(Collectors.toList())
                .toArray();
        params.put("utxos", uTxOs);
        return this;
    }
}
