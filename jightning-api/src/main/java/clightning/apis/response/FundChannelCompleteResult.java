package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FundChannelCompleteResult {
    @JsonProperty("channel_id")
    private String channelId;

    @JsonProperty("commitments_secured")
    private boolean commitmentsSecured;
}
