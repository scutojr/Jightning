package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CheckResult {

    @JsonProperty("command_to_check")
    String command;
}
