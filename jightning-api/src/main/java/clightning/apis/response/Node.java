package clightning.apis.response;

import clightning.apis.BasedNetwork;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Optional;

/**
 * Response of {@link BasedNetwork#listNodes}
 */
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
