package clightning.apis.response;

import clightning.apis.Output;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class Peer {

    public static class OutputDeserializer extends JsonDeserializer<Output[]> {
        @Override
        public Output[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            List<Output> array = new ArrayList<>();

            while (!p.isClosed()) {
                JsonToken token = p.nextToken();
                if (JsonToken.END_OBJECT.equals(token)) {
                    break;
                } else if (JsonToken.FIELD_NAME.equals(token)) {
                    String address = p.currentName();
                    token = p.nextToken();
                    Output output;
                    if (token.isNumeric()) {
                        output = new Output(address, p.getValueAsLong());
                    } else {
                        output = new Output(address, p.getValueAsString());
                    }
                    array.add(output);
                }
            }
            Output[] res = array.toArray(new Output[array.size()]);
            return res;
        }
    }

    private String id;
    private boolean connected;

    @JsonSetter("netaddr")
    private Optional<String[]> netaddr; // [ "172.18.0.4:9735"]

    @JsonSetter("globalfeatures")
    private Optional<String> globalfeatures;

    @JsonSetter("localfeatures")
    private Optional<String> localfeatures;

    private Optional<String> features;
    private Optional<LogResult.Log[]> log;
    private Channel[] channels;

    @Data
    public static class Channel {
        private String state; // CLOSINGD_COMPLETE

        @JsonSetter("scratch_txid")
        private Optional<String> scratch_txid;

        private Optional<String> owner;

        @JsonSetter("short_channel_id")
        private Optional<String> short_channel_id;

        private Optional<Integer> direction; // 0 or 1

        @JsonSetter("channel_id")
        private String channel_id;

        @JsonSetter("funding_txid")
        private String funding_txid;

        @JsonSetter("private")
        private boolean isPrivate;
        /**
         * ": {
         * "036c0793141c045a9e1e50efaa2740def367800580ecad7d31268103f9b9e97472":0, // msatoshi
         * "03b27d2edad44bcf65b92605031a5577843b336551deb480e38537a845da6c7aec":10000000000 //msatoshi
         * },
         */
        @JsonDeserialize(using = OutputDeserializer.class)
        @JsonSetter("funding_allocation_msat")
        private Output[] funding_allocation_msat;

        /**
         * {
         * "036c0793141c045a9e1e50efaa2740def367800580ecad7d31268103f9b9e97472":"0msat",
         * "03b27d2edad44bcf65b92605031a5577843b336551deb480e38537a845da6c7aec":"10000000000msat"
         * },
         */
        @JsonDeserialize(using = OutputDeserializer.class)
        @JsonSetter("funding_msat")
        private Output[] funding_msat;

        @JsonSetter("msatoshi_to_us")
        private long msatoshi_to_us;

        @JsonSetter("to_us_msat")
        private String to_us_msat;

        @JsonSetter("msatoshi_to_us_min")
        private long msatoshi_to_us_min;

        @JsonSetter("min_to_us_msat")
        private String min_to_us_msat;

        @JsonSetter("msatoshi_to_us_max")
        private long msatoshi_to_us_max;

        @JsonSetter("max_to_us_msat")
        String max_to_us_msat;

        @JsonSetter("msatoshi_total")
        long msatoshi_total;

        @JsonSetter("total_msat")
        String total_msat;

        @JsonSetter("dust_limit_satoshis")
        long dust_limit_satoshis;

        @JsonSetter("dust_limit_msat")
        String dust_limit_msat;

        @JsonSetter("max_htlc_value_in_flight_msat")
        long max_htlc_value_in_flight_msat;

        @JsonSetter("max_total_htlc_in_msat")
        String max_total_htlc_in_msat;

        @JsonSetter("their_channel_reserve_satoshis")
        long their_channel_reserve_satoshis;

        @JsonSetter("their_reserve_msat")
        String their_reserve_msat;

        @JsonSetter("our_channel_reserve_satoshis")
        long our_channel_reserve_satoshis;

        @JsonSetter("our_reserve_msat")
        String our_reserve_msat;

        @JsonSetter("spendable_msatoshi")
        long spendable_msatoshi;

        @JsonSetter("spendable_msat")
        String spendable_msat;

        @JsonSetter("htlc_minimum_msat")
        long htlc_minimum_msat;

        @JsonSetter("minimum_htlc_in_msat")
        String minimum_htlc_in_msat;

        @JsonSetter("their_to_self_delay")
        int their_to_self_delay;

        @JsonSetter("our_to_self_delay")
        int our_to_self_delay;

        @JsonSetter("max_accepted_htlcs")
        int max_accepted_htlcs;
        /**
         * ": [
         * "CHANNELD_AWAITING_LOCKIN:Reconnected, and reestablished.",
         * "CLOSINGD_SIGEXCHANGE:We agreed on a closing fee of 183 satoshi for tx:82caf0783590a47d33c9a8509bfd9f9b259b5cf06cab8e9ab6bd741abc11a52c"
         * ],
         */
        String[] status;

        @JsonSetter("in_payments_offered")
        long in_payments_offered;

        @JsonSetter("in_msatoshi_offered")
        long in_msatoshi_offered;

        @JsonSetter("in_offered_msat")
        String in_offered_msat;

        @JsonSetter("in_payments_fulfilled")
        long in_payments_fulfilled;

        @JsonSetter("in_msatoshi_fulfilled")
        long in_msatoshi_fulfilled;

        @JsonSetter("in_fulfilled_msat")
        String in_fulfilled_msat;

        @JsonSetter("out_payments_offered")
        long out_payments_offered;

        @JsonSetter("out_msatoshi_offered")
        long out_msatoshi_offered;

        @JsonSetter("out_offered_msat")
        String out_offered_msat;

        @JsonSetter("out_payments_fulfilled")
        long out_payments_fulfilled;

        @JsonSetter("out_msatoshi_fulfilled")
        long out_msatoshi_fulfilled;

        @JsonSetter("out_fulfilled_msat")
        String out_fulfilled_msat;

        HTLC[] htlcs;
    }

    @Data
    public static class HTLC {

        private String direction;
        private long id;
        private long msatoshi;

        @JsonSetter("amount_msat")
        private String amount_msat;

        private long expiry;

        @JsonSetter("payment_hash")
        private String payment_hash;

        private String state;

        @JsonSetter("local_trimmed")
        private boolean local_trimmed = false;
    }
}
