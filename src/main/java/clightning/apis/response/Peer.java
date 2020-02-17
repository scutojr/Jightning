package clightning.apis.response;

import clightning.apis.Output;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class Peer {
    private String id;
    private boolean connected;

    @JsonProperty("netaddr")
    private Optional<String[]> netaddr; // [ "172.18.0.4:9735"]

    @JsonProperty("globalfeatures")
    private Optional<String> globalfeatures;

    @JsonProperty("localfeatures")
    private Optional<String> localfeatures;

    private Optional<String> features;
    private Optional<LogResult.Log[]> log;
    private Channel[] channels;

    @Data
    public static class Channel {
        private String state; // CLOSINGD_COMPLETE

        @JsonProperty("scratch_txid")
        private Optional<String> scratch_txid;

        private Optional<String> owner;

        @JsonProperty("short_channel_id")
        private Optional<String> short_channel_id;

        private Optional<Integer> direction; // 0 or 1

        @JsonProperty("channel_id")
        private String channel_id;

        @JsonProperty("funding_txid")
        private String funding_txid;

        @JsonProperty("private")
        private boolean isPrivate;
        /**
         * ": {
         * "036c0793141c045a9e1e50efaa2740def367800580ecad7d31268103f9b9e97472":0, // msatoshi
         * "03b27d2edad44bcf65b92605031a5577843b336551deb480e38537a845da6c7aec":10000000000 //msatoshi
         * },
         */
        @JsonProperty("funding_allocation_msat")
        private Output[] funding_allocation_msat;

        /**
         * {
         * "036c0793141c045a9e1e50efaa2740def367800580ecad7d31268103f9b9e97472":"0msat",
         * "03b27d2edad44bcf65b92605031a5577843b336551deb480e38537a845da6c7aec":"10000000000msat"
         * },
         */
        @JsonProperty("funding_msat")
        private Output[] funding_msat;

        @JsonProperty("msatoshi_to_us")
        private long msatoshi_to_us;

        @JsonProperty("to_us_msat")
        private String to_us_msat;

        @JsonProperty("msatoshi_to_us_min")
        private long msatoshi_to_us_min;

        @JsonProperty("min_to_us_msat")
        private String min_to_us_msat;

        @JsonProperty("msatoshi_to_us_max")
        private long msatoshi_to_us_max;

        @JsonProperty("max_to_us_msat")
        String max_to_us_msat;

        @JsonProperty("msatoshi_total")
        long msatoshi_total;

        @JsonProperty("total_msat")
        String total_msat;

        @JsonProperty("dust_limit_satoshis")
        long dust_limit_satoshis;

        @JsonProperty("dust_limit_msat")
        String dust_limit_msat;

        @JsonProperty("max_htlc_value_in_flight_msat")
        BigDecimal max_htlc_value_in_flight_msat;

        @JsonProperty("max_total_htlc_in_msat")
        String max_total_htlc_in_msat;

        @JsonProperty("their_channel_reserve_satoshis")
        long their_channel_reserve_satoshis;

        @JsonProperty("their_reserve_msat")
        String their_reserve_msat;

        @JsonProperty("our_channel_reserve_satoshis")
        long our_channel_reserve_satoshis;

        @JsonProperty("our_reserve_msat")
        String our_reserve_msat;

        @JsonProperty("spendable_msatoshi")
        long spendable_msatoshi;

        @JsonProperty("spendable_msat")
        String spendable_msat;

        @JsonProperty("htlc_minimum_msat")
        long htlc_minimum_msat;

        @JsonProperty("minimum_htlc_in_msat")
        String minimum_htlc_in_msat;

        @JsonProperty("their_to_self_delay")
        int their_to_self_delay;

        @JsonProperty("our_to_self_delay")
        int our_to_self_delay;

        @JsonProperty("max_accepted_htlcs")
        int max_accepted_htlcs;
        /**
         * ": [
         * "CHANNELD_AWAITING_LOCKIN:Reconnected, and reestablished.",
         * "CLOSINGD_SIGEXCHANGE:We agreed on a closing fee of 183 satoshi for tx:82caf0783590a47d33c9a8509bfd9f9b259b5cf06cab8e9ab6bd741abc11a52c"
         * ],
         */
        String[] status;

        @JsonProperty("in_payments_offered")
        long in_payments_offered;

        @JsonProperty("in_msatoshi_offered")
        long in_msatoshi_offered;

        @JsonProperty("in_offered_msat")
        String in_offered_msat;

        @JsonProperty("in_payments_fulfilled")
        long in_payments_fulfilled;

        @JsonProperty("in_msatoshi_fulfilled")
        long in_msatoshi_fulfilled;

        @JsonProperty("in_fulfilled_msat")
        String in_fulfilled_msat;

        @JsonProperty("out_payments_offered")
        long out_payments_offered;

        @JsonProperty("out_msatoshi_offered")
        long out_msatoshi_offered;

        @JsonProperty("out_offered_msat")
        String out_offered_msat;

        @JsonProperty("out_payments_fulfilled")
        long out_payments_fulfilled;

        @JsonProperty("out_msatoshi_fulfilled")
        long out_msatoshi_fulfilled;

        @JsonProperty("out_fulfilled_msat")
        String out_fulfilled_msat;

        HTLC[] htlcs;
    }

    @Data
    public static class HTLC {

        private String direction;
        private long id;
        private long msatoshi;

        @JsonProperty("amount_msat")
        private String amount_msat;

        private long expiry;

        @JsonProperty("payment_hash")
        private String payment_hash;

        private String state;

        @JsonProperty("local_trimmed")
        private boolean local_trimmed = false;
    }
}
