package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FundChannel {
    private String tx;

    @JsonProperty("txid")
    private String txId;

    @JsonProperty("channel_id")
    private String channelId;
}
