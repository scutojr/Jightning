package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Optional;

@Data
public class Transaction {
    private String hash;

    @JsonProperty("rawtx")
    private String rawTx;

    @JsonProperty("blockheight")
    private int blockHeight;

    @JsonProperty("txindex")
    private int txIndex;

    @JsonProperty("locktime")
    private long lockTime;

    private int version;

    private Optional<String[]> type;
    private Optional<String> channel; // short channel id

    private Input[] inputs;
    private Output[] outputs;

    @Data
    public static class Input {
        @JsonProperty("txid")
        private String txId;

        private int index;
        private long sequence;
        private Optional<String> type;
        private Optional<String> channel;
    }

    @Data
    public static class Output {
        private int index;
        private String satoshis;
        private String scriptPubKey;

        private Optional<String> type;
        private Optional<String> channel;
    }
}
