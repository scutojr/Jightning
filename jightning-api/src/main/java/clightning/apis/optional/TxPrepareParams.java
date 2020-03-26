package clightning.apis.optional;

import clightning.apis.UTxO;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TxPrepareParams extends OptionalParams {
    public TxPrepareParams setFeeRate(FeeRate feeRate) {
        params.put("feerate", feeRate.toString());
        return this;
    }

    public TxPrepareParams setMinConf(int minConfirmation) {
        params.put("minconf", minConfirmation);
        return this;
    }

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
