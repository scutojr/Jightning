package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CloseResult {
      private String tx;

      @JsonProperty("txid")
      private String txId;

      private String type; // TODO: classify the type such as "mutual"
}
