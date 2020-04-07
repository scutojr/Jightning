package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response of {@link clightning.apis.BasedUtility#checkMessage}
 */
@Data
public class CheckMessageResult {
    @JsonProperty("pubkey")
    private String pubKey;

    private boolean verified;
}
