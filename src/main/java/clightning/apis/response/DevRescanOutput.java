package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class DevRescanOutput {
        @JsonSetter("txid")
        private String txId;

        private int output;

        @JsonSetter("oldstate")
        private int oldState;

        @JsonSetter("newstate")
        private int newState;
}
