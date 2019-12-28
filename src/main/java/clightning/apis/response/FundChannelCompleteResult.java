package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class FundChannelCompleteResult {
    @JsonSetter("channel_id")
    private String channelId;

    @JsonSetter("commitmentsSecured")
    private boolean commitments_secured;
}
