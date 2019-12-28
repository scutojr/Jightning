package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.Optional;

@Data
public class FundChannelStartResult {
    @JsonSetter("funding_address")
    private String fundingAddress;

    @JsonSetter("scriptpubkey")
    private String scriptPubKey;

    // TODO: ensure the existence of this field
    @JsonSetter("close_to")
    private Optional<String> closeTo;
}
