package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Optional;

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

    /**
     * The following fields are optional:
     *
     * fallbacks: array of fallback address object containing a hex string, and both type and addr if it is recognized
     *            as one of P2PKH, P2SH, P2WPKH, or P2WSH.
     * routes: an array of routes. Each route is an arrays of objects, each containing pubkey, short_channel_id,
     *         fee_base_msat, fee_proportional_millionths and cltv_expiry_delta.
     *         [
     *           [
     *             {
     *               pubkey: xxxyy // the node id
     *               "short_channel_id"; // String
     *               "fee_base_msat": <u64>,
     *               "fee_proportional_millionths": <u64>,
     *               "cltv_expiry_delta": <num> // int?
     *             }
     *           ]
     *         ]
     * extra: an array of objects representing unknown fields, each with one-character tag and a data bech32 string.
     *     [
     *         {
     *             tag: <String>,
     *             data: <String>
     *         }
     *     ]
     */
}
