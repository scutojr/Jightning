package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class CheckMessageResult {
    @JsonSetter("pubkey")
    private String pubKey;

    private boolean verified;
}
