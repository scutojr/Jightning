package clightning.apis.response;

import clightning.apis.optional.FundChannelStartParams;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Optional;

/**
 * Response of {@link clightning.apis.BasedChannel#fundChannelStart}
 */
@Data
public class FundChannelStartResult {
    @JsonProperty("funding_address")
    private String fundingAddress;

    @JsonProperty("scriptpubkey")
    private String scriptPubKey;

    @JsonProperty("close_to")
    private Optional<String> closeTo;
}
