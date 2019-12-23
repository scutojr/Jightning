package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class FundChannel {
    private String tx;

    @JsonSetter("txid")
    private String txId;

    @JsonSetter("channel_id")
    private String channelId;
}
