package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.Optional;

@Data
public class PayStatus {
    private String bolt11;
    private long msatoshi;

    @JsonSetter("amount_msat")
    private String amountMsat;
    private String destination;

    private Optional<String> label;
    private Optional<String> shadow;

    @JsonSetter("routehint_modifications")
    private Optional<String> routehintModifications;

    @JsonSetter("local_exclusions")
    private Optional<String> localExclusions;

    private Attempt[] attempts;

    @Data
    public static class RouteHint {
        private String id;
        private String channel;

        @JsonSetter("fee_base_msat")
        private long feeBaseMsat;

        @JsonSetter("fee_proportional_millionths")
        private long feeProportionalMillionths;

        @JsonSetter("cltv_expiry_delta")
        private long cltvExpiryDelta;
    }

    @Data
    public static class Attempt {
        private String strategy;
        private String start_time;

        @JsonSetter("age_in_seconds")
        private long age_in_seconds;

        @JsonSetter("end_time")
        private Optional<String> endTime;

        @JsonSetter("duration_in_seconds")
        private Optional<Long> durationInSeconds;

        @JsonSetter("routehint")
        private Optional<RouteHint[]> routeHints;

        @JsonSetter("excluded_nodes_or_channels")
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
