package clightning.apis.response;

import clightning.apis.BasedChannel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response of {@link BasedChannel#listChannels}
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
