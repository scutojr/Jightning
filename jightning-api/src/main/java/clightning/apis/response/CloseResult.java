package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CloseResult {
      public enum Type {
            mutual, // we could negotiate a close with the peer
            unilateral; //  the force flag was set and we had to close the channel without waiting for the counterparty
      }
      private String tx;

      @JsonProperty("txid")
      private String txId;

      private Type type;
}
