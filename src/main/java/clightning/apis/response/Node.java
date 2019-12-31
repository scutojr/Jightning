package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.Optional;

@Data
public class Node {
    @JsonSetter("nodeid")
    private String nodeId;

    private Optional<String> alias;
    private Optional<String> color;

    @JsonSetter("last_timestamp")
    private Optional<Integer> lastTimestamp;

    @JsonSetter("globalfeatures")
    private Optional<String> globalFeatures;

    private Optional<String> features;
    private Optional<LightningDaemonInfo.Address[]> addresses;
}
