package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FeeRate {

    @JsonProperty("onchain_fee_estimates")
    private OnChainFee onChainFeeEstimates;

    @JsonProperty("perkw")
    private PerUnitFee perKw;

    @JsonProperty("perkb")
    private PerUnitFee perKb;

    @Data
    public static class OnChainFee {
        @JsonProperty("opening_channel_satoshis")
        private int openingChannelSatoshis;

        @JsonProperty("mutual_close_satoshis")
        private int mutualCloseSatoshis;

        @JsonProperty("unilateral_close_satoshis")
        private int unilateralCloseSatoshis;
    }

    @Data
    public static class PerUnitFee {
        private int urgent;
        private int normal;
        private int slow;

        @JsonProperty("min_acceptable")
        private long minAcceptable;

        @JsonProperty("max_acceptable")
        private long maxAcceptable;
    }
}
