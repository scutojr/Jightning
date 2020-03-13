package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Optional;

@Data
public class Node {
    @JsonProperty("nodeid")
    private String nodeId;

    private Optional<String> alias;
    private Optional<String> color;

    @JsonProperty("last_timestamp")
    private Optional<Integer> lastTimestamp;

    @JsonProperty("globalfeatures")
    private Optional<String> globalFeatures;

    private Optional<String> features;
    private Optional<LightningDaemonInfo.Address[]> addresses;
}
