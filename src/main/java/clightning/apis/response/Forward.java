package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Optional;

@Data
public class Forward {
    @JsonProperty("payment_hash")
    private String paymentHash;

    @JsonProperty("in_channel")
    private String inChannel;

    @JsonProperty("out_channel")
    private String outChannel;

    @JsonProperty("in_msatoshi")
    private long inMsatoshi;

    @JsonProperty("in_msat")
    private String inMsat;

    @JsonProperty("out_msatoshi")
    private Optional<Long> outMsatoshi;

    @JsonProperty("out_msat")
    private Optional<String> outMsat;

    private Optional<Long> fee;

    @JsonProperty("fee_msat")
    private Optional<String> feeMsat;

    private String status;

    @JsonProperty("failcode")
    private Optional<Integer> failCode;

    @JsonProperty("failreason")
    private Optional<String> failReason;

    @JsonProperty("received_time")
    private Optional<Double> receivedTime;

    @JsonProperty("resolved_time")
    private Optional<Double> resolvedTime;
}
