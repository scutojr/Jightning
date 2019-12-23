package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class FeeRate {

    @JsonSetter("onchain_fee_estimates")
    private OnChainFee onChainFeeEstimates;

    @JsonSetter("perkw")
    private PerUnitFee perKw;

    @JsonSetter("perkb")
    private PerUnitFee perKb;

    @Data
    public static class OnChainFee {
        @JsonSetter("opening_channel_satoshis")
        private int openingChannelSatoshis;

        @JsonSetter("mutual_close_satoshis")
        private int mutualCloseSatoshis;

        @JsonSetter("unilateral_close_satoshis")
        private int unilateralCloseSatoshis;
    }

    @Data
    public static class PerUnitFee {
        private int urgent;
        private int normal;
        private int slow;

        @JsonSetter("min_acceptable")
        private long minAcceptable;

        @JsonSetter("max_acceptable")
        private long maxAcceptable;
    }
}
