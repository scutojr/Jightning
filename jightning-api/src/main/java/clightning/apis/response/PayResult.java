package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PayResult {

      private int id;

      @JsonProperty("payment_hash")
      private String paymentHash;

      private String destination;
      private long msatoshi;

      @JsonProperty("amount_msat")
      private String amountMsat;

      @JsonProperty("msatoshi_sent")
      private long msatoshiSent;

      @JsonProperty("amount_sent_msat")
      private String amountSentMsat;

      @JsonProperty("created_at")
      private long createdAt;

      private Status status;

      @JsonProperty("payment_preimage")
      private String paymentPreimage;
      private String bolt11;

      public enum Status {
            pending,
            complete,
            failed;
      }
}
