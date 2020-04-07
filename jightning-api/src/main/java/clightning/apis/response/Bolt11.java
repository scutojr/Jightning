package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Optional;

/**
 * Response of {@link clightning.apis.BasedPayment#decodePay}
 */
@Data
public class Bolt11 {
    private String currency;

    @JsonProperty("created_at")
    private long createdAt;

    private int expiry; // the number of seconds this is valid after timestamp.
    private String payee; // the public key of the recipient.

    private Optional<Long> msatoshi;

    @JsonProperty("amount_msat")
    private Optional<String> amountMsat;

    private String description;

    @JsonProperty("min_final_cltv_expiry")
    private int minFinalCltvExpiry;

    private String features;

    @JsonProperty("payment_hash")
    private String paymentHash;

    private String signature;

    private Optional<Fallback[]> fallbacks;
    private Optional<Route[][]> routes;
    private Optional<Extra[]> extra;

    @Data
    public static class Extra {
        private  String tag;
        private String data;
    }

    @Data
    public static class Fallback {
        private String type; // P2PKH, P2SH, P2WPKH, P2WSH
        private String addr;
        private String hex;
    }

    @Data
    public static class Route {
        @JsonProperty("pubkey")
        private String pubKey;

        @JsonProperty("short_channel_id")
        private String shortChannelId;

        @JsonProperty("fee_base_msat")
        private long feeBaseMsat;

        @JsonProperty("fee_proportional_millionths")
        private long feeProportionalMillionths;

        @JsonProperty("cltv_expiry_delta")
        private int cltvExpiryDelta;
    }
}
