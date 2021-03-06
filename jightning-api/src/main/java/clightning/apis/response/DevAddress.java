package clightning.apis.response;

import clightning.apis.BasedDeveloper;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response of {@link BasedDeveloper#devListAddrs}
 */
@Data
public class DevAddress {
    @JsonProperty("keyidx")
    private int keyIdx;

    @JsonProperty("pubkey")
    private String pubKey;

    private String p2sh;

    @JsonProperty("p2sh_redeemscript")
    private String p2shRedeemScript;

    private String bech32;

    @JsonProperty("bech32_redeemscript")
    private String bech32RedeemScript;
}
