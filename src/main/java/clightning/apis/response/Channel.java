package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * {
 *   "source": "0265197a63b374393904a418940500fb840c62f0a0a672ecb69561b491ab241dec",
 *   "destination": "03b27d2edad44bcf65b92605031a5577843b336551deb480e38537a845da6c7aec",
 *   "short_channel_id": "204x1x0",
 *   "public": false,
 *   "satoshis": 10000000,
 *   "amount_msat": "10000000000msat",
 *   "message_flags": 1,
 *   "channel_flags": 0,
 *   "active": true,
 *   "last_update": 1577122186,
 *   "base_fee_millisatoshi": 1,
 *   "fee_per_millionth": 10,
 *   "delay": 6,
 *   "htlc_minimum_msat": "0msat",
 *   "htlc_maximum_msat": "4294967295msat"
 * },
 */
@Data
public class Channel {
     private String source;
     private String destination;

     @JsonProperty("short_channel_id")
     private String shortChannelId;

     @JsonProperty("public")
     private boolean isPublic;

     private long satoshis;

     @JsonProperty("amount_msat")
     private String amountMsat;

     @JsonProperty("message_flags")
     private int messageFlags;

     @JsonProperty("channel_flags")
     private int channelFlags;

     private boolean active;

     @JsonProperty("last_update")
     private int lastUpdate;

     @JsonProperty("base_fee_millisatoshi")
     private int baseFeeMillisatoshi;

     @JsonProperty("fee_per_millionth")
     private int feePerMillionth;
     private int delay;

     @JsonProperty("htlc_minimum_msat")
     private String htlcMinimumMsat;

     @JsonProperty("htlc_maximum_msat")
     private String htlcMaximumMsat;
}
