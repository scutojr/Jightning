package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class DetailedInvoice {
   private String label;
   private String bolt11;

   @JsonSetter("payment_hash")
   private String paymentHash;

   private long msatoshi;

   @JsonSetter("amount_msat")
   private String amountMsat;

   private String status;
   private String description;

   @JsonSetter("expires_at")
   private long expiresAt;
}
