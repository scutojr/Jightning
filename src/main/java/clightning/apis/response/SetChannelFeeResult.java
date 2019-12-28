package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class SetChannelFeeResult {
    private long base;
    private int ppm;
    private Channel[] channels;

    @Data
    public static class Channel {
        @JsonSetter("peer_id")
        private String peerId;

        @JsonSetter("channel_id")
        private String channelId;
    }
}
