package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Route {
    private String id;
    private String channel;
    private int direction;
    private long msatoshi;

    @JsonProperty("amount_msat")
    private String amountMsat;

    private int delay;
}
