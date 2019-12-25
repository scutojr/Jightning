package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class Route {
    private String id;
    private String channel;
    private int direction;
    private long msatoshi;

    @JsonSetter("amount_msat")
    private String amountMsat;

    private int delay;
}
