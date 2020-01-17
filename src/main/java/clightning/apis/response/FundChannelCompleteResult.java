package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class FundChannelCompleteResult {
    @JsonSetter("channel_id")
    private String channelId;

    @JsonSetter("commitments_secured")
    private boolean commitmentsSecured;
}
