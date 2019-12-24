package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class SimpleInvoice {
      @JsonSetter("payment_hash")
      private String paymentHash;

      // TODO: unify the type of timestamp value
      @JsonSetter("expires_at")
      private long expiresAt;

      private String bolt11;

      @JsonSetter("warning_capacity")
      private String warningCapacity;
}
