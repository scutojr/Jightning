package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Optional;

@Data
public class SimpleInvoice {
      @JsonProperty("payment_hash")
      private String paymentHash;

      // TODO: unify the type of timestamp value
      @JsonProperty("expires_at")
      private long expiresAt;

      private String bolt11;

      @JsonProperty("warning_capacity")
      private Optional<String> warningCapacity;
}
