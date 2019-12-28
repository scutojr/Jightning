package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class CloseResult {
      private String tx;

      @JsonSetter("txid")
      private String txId;

      private String type; // TODO: classify the type sucha as "mutual"
}
