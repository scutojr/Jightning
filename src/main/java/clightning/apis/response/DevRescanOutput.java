package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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
