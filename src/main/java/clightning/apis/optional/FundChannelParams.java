package clightning.apis.optional;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FundChannelParams extends OptionalParams {
    public static class FeeRate {
        private String value;

        private FeeRate(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static FeeRate urgent() {
            return new FeeRate("urgent");
        }

        public static FeeRate normal() {
            return new FeeRate("normal");
        }

        public static FeeRate slow() {
            return new FeeRate("slow");
        }

        public static FeeRate perKw(long satoshi) {
            return new FeeRate(satoshi + "perkw");
        }

        public static FeeRate perKb(long satoshi) {
            return new FeeRate(satoshi + "perkb");
        }
    }

    public static class UTxO {
        private String txId;
        private int vOut;

        public UTxO(String txId, int vOut) {
            this.txId = txId;
            this.vOut = vOut;
        }

        @Override
        public String toString() {
            return txId + ":" + vOut;
        }
    }

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
     * TODO: ensure the array format is acceptable to the lightning feerate rpc
     *
     * @param uTxO
     * @return
     */
    public FundChannelParams setUTxOs(UTxO... uTxO) {
        String[] uTxOs = (String[]) Arrays.asList(uTxO)
                .stream()
                .map(t -> t.toString())
                .collect(Collectors.toList())
                .toArray();
        params.put("utxos", uTxOs);
        return this;
    }
}
