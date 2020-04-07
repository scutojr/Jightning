package clightning.apis.response;

import clightning.apis.BasedDeveloper;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response of {@link BasedDeveloper#devRescanOutputs}
 */
@Data
public class DevRescanOutput {
    @JsonProperty("txid")
    private String txId;

    private int output;

    @JsonProperty("oldstate")
    private int oldState;

    @JsonProperty("newstate")
    private int newState;
}
