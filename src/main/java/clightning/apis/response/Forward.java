package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.Optional;

@Data
public class Forward {
    @JsonSetter("payment_hash")
    private String paymentHash;

    @JsonSetter("in_channel")
    private String inChannel;

    @JsonSetter("out_channel")
    private String outChannel;

    @JsonSetter("in_msatoshi")
    private long inMsatoshi;

    @JsonSetter("in_msat")
    private String inMsat;

    @JsonSetter("out_msatoshi")
    private Optional<Long> outMsatoshi;

    @JsonSetter("out_msat")
    private Optional<String> outMsat;

    private Optional<Long> fee;

    @JsonSetter("fee_msat")
    private Optional<String> feeMsat;

    private String status;

    @JsonSetter("failcode")
    private Optional<Integer> failCode;

    @JsonSetter("failreason")
    private Optional<String> failReason;

    @JsonSetter("received_time")
    private Optional<Double> receivedTime;

    @JsonSetter("resolved_time")
    private Optional<Double> resolvedTime;
}
