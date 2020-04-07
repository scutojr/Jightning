package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response of {@link clightning.apis.BasedChannel#setChannelFee}
 */
@Data
public class SetChannelFeeResult {
    private long base;
    private int ppm;
    private Channel[] channels;

    @Data
    public static class Channel {
        @JsonProperty("peer_id")
        private String peerId;

        @JsonProperty("channel_id")
        private String channelId;

        @JsonProperty("short_channel_id")
        private String shortChannelId;
    }
}
