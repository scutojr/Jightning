package clightning.apis.response;

import clightning.apis.BasedBitcoin;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response of {@link BasedBitcoin#newAddr}
 */
public class LightningAddress {
    private String bech32;

    @JsonProperty("p2sh-segwit")
    private String p2shSegwit;

    public String getBech32() {
        return bech32;
    }

    public String getP2shSegwit() {
        return p2shSegwit;
    }

    public void setBech32(String bech32) {
        this.bech32 = bech32;
    }

    public void setP2shSegwit(String p2shSegwit) {
        this.p2shSegwit = p2shSegwit;
    }
}
