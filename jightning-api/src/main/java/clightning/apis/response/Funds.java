package clightning.apis.response;

import clightning.apis.BasedUtility;
import clightning.apis.ChannelState;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Optional;

/**
 * Response of {@link BasedUtility#listFunds()}
 */
@Data
public class Funds {
    private TxOutput[] outputs;
    private Channel[] channels;

    /**
     * {
     *   "peer_id": "0265197a63b374393904a418940500fb840c62f0a0a672ecb69561b491ab241dec",
     *   "connected": true,
     *   "state": "CHANNELD_AWAITING_LOCKIN",
     *   "channel_sat": 10000000,
     *   "our_amount_msat": "10000000000msat",
     *   "channel_total_sat": 10000000,
     *   "amount_msat": "10000000000msat",
     *   "funding_txid": "4d99cf30a184c5858b313b15298f6c1417d71e0b0d97c6fc690d7e2ce7c8a69a",
     *   "funding_output": 0
     * }
     */
    @Data
    public static class Channel {
        private boolean connected;
        private ChannelState state;

        @JsonProperty("short_channel_id")
        private Optional<String> shortChannelId; // only available when state is CHANNELD_NORMAL

        @JsonProperty("peer_id")
        private String peerId;

        @JsonProperty("channel_sat")
        private long channelSat;

        @JsonProperty("our_amount_msat")
        private String ourAmountMsat;

        @JsonProperty("channel_total_sat")
        private long channelTotalSat;

        @JsonProperty("amount_msat")
        private String amountMsat;

        @JsonProperty("funding_txid")
        private String fundingTxId;

        @JsonProperty("funding_output")
        private String fundingOutput;
    }

    public enum TxOutputStatus {
        spent,
        confirmed,
        unconfirmed;
    }

    @Data
    public static class TxOutput {

        @JsonProperty("txid")
        private String txId;

        private int output;
        private long value;

        @JsonProperty("amount_msat")
        private String amountMsat;

        private Optional<String> address;
        private Optional<TxOutputStatus> status;

        @JsonProperty("blockheight")
        private Optional<Integer> blockHeight;
    }
}
