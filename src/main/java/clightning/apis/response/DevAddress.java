package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class DevAddress {
        @JsonSetter("keyIdx")
        private int keyidx;

        @JsonSetter("pubKey")
        private String pubkey;

        private String p2sh;

        @JsonSetter("p2sh_redeemscript")
        private String p2shRedeemScript;

        private String bech32;

        @JsonSetter("bech32_redeemscript")
        private String bech32RedeemScript;
}
