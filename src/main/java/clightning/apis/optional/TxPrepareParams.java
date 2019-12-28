package clightning.apis.optional;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TxPrepareParams extends OptionalParams {
    public TxPrepareParams setFeeRate(FeeRate feeRate) {
        params.put("feerate", feeRate.toString());
        return this;
    }

    public TxPrepareParams setMinConfirmation(int minConf) {
        params.put("minconf", minConf);
        return this;
    }

    public TxPrepareParams setUTxO(UTxO... uTxO) {
        String[] uTxOs = (String[]) Arrays.asList(uTxO)
                .stream()
                .map(t -> t.toString())
                .collect(Collectors.toList())
                .toArray();
        params.put("utxos", uTxOs);
        return this;
    }
}
