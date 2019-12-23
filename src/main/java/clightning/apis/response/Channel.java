package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

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
public class Channel {
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
