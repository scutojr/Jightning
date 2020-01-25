package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CheckMessageResult {
    @JsonProperty("pubkey")
    private String pubKey;

    private boolean verified;
}
