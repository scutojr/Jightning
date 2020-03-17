package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Optional;

@Data
public class SimpleInvoice {
      @JsonProperty("payment_hash")
      private String paymentHash;

      @JsonProperty("expires_at")
      private int expiresAt; // seconds

      private String bolt11;

      @JsonProperty("warning_capacity")
      private Optional<String> warningCapacity;
}
