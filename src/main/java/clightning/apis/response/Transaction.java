package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.Optional;

@Data
public class Transaction {
    private String hash;

    @JsonSetter("rawtx")
    private String rawTx;

    @JsonSetter("blockheight")
    private int blockHeight;

    @JsonSetter("txindex")
    private int txIndex;

    @JsonSetter("locktime")
    private long lockTime;

    private int version;

    private Optional<String[]> type;
    private Optional<String> channel; // short channel id

    private Input[] inputs;
    private Output[] outputs;

    @Data
    public static class Input {
        @JsonSetter("txid")
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
