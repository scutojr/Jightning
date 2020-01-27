package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Optional;

@Data
public class FundChannelStartResult {
    @JsonProperty("funding_address")
    private String fundingAddress;

    @JsonProperty("scriptpubkey")
    private String scriptPubKey;

    // TODO: ensure the existence of this field
    @JsonProperty("close_to")
    private Optional<String> closeTo;
}
