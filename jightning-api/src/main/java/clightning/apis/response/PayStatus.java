package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.Optional;

/**
 * Response of {@link clightning.apis.BasedPlugin#payStatus}
 */
@Data
public class PayStatus {
    private String bolt11;
    private long msatoshi;

    @JsonProperty("amount_msat")
    private String amountMsat;
    private String destination;

    private Optional<String> label;
    private Optional<String> shadow;

    @JsonProperty("routehint_modifications")
    private Optional<String> routehintModifications;

    @JsonProperty("local_exclusions")
    private Optional<String> localExclusions;

    private Attempt[] attempts;

    @Data
    public static class RouteHint {
        private String id;
        private String channel;

        @JsonProperty("fee_base_msat")
        private long feeBaseMsat;

        @JsonProperty("fee_proportional_millionths")
        private long feeProportionalMillionths;

        @JsonProperty("cltv_expiry_delta")
        private long cltvExpiryDelta;
    }

    @Data
    public static class Attempt {
        private String strategy;
        private String start_time;

        @JsonProperty("age_in_seconds")
        private long age_in_seconds;

        @JsonProperty("end_time")
        private Optional<String> endTime;

        @JsonProperty("duration_in_seconds")
        private Optional<Long> durationInSeconds;

        @JsonProperty("routehint")
        private Optional<RouteHint[]> routeHints;

        @JsonProperty("excluded_nodes_or_channels")
        private Optional<String[]> excludedNodesOrChannels;

        private Optional<JsonNode> route;
        private Optional<JsonNode> failure;
        private Optional<JsonNode> success;

        public boolean isLookupFailed() {
            return route.get() == null;
        }

        public boolean isFailed() {
            return failure.get() == null;
        }
    }
}
