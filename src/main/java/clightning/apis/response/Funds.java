package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

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
        private String state;

        @JsonSetter("peer_id")
        private String peerId;

        @JsonSetter("channel_sat")
        private long channelSat;

        @JsonSetter("our_amount_msat")
        private String ourAmountMsat;

        @JsonSetter("channel_total_sat")
        private long channelTotalSat;

        @JsonSetter("amount_msat")
        private String amountMsat;

        @JsonSetter("funding_txid")
        private String fundingTxId;

        @JsonSetter("funding_output")
        private String fundingOutput;
    }

    /**
     *{
     *   "txid": "9790451a10359356138ba005632636a42d2f39cb3c32802d14eb028994965033",
     *   "output": 0,
     *   "value": 1000000000,
     *   "amount_msat": "1000000000000msat",
     *   "address": "bcrt1qlcfh4mjqtelj04a9r874pypj7lxd4z59z66u7c",
     *   "status": "confirmed",
     *   "blockheight": 102
     *}
     * TODO: check the json definition for Output in c-lightning code
     */
    @Data
    public static class TxOutput {

        @JsonSetter("txid")
        private String txId;

        private int output;
        private long value;

        @JsonSetter("amount_msat")
        private String amountMsat;

        private String address;
        private String status;

        @JsonSetter("blockheight")
        private int blockHeight;
    }
}
