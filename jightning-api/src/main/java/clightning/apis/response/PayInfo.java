package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Optional;

/**
 *
 *       {
 *       "payment_hash": "030bc2f32e0bb31b012ce73d710a78235e6354fe20cbb615cab3362fa223e632",
 *       "destination": "022a3fd5dde424d173eb7daaeccd5a55369c11fce0325a684dc68b36b2b5b830af",
 *       "amount_msat": "1000000msat",
 *       "status": "complete",
 *       "payment_preimage": "991ec522f9f0420b579d8e7444a309e3d74e47333e308ebfb4e1c820807eee20",
 *        "amount_sent_msat": "1000000msat
 *       },
 *       {
 *          "bolt11": "lnbcrt10u1p0rtfdepp5ga0hpl6yp867mfsgk3umjgwpxa4x99nrzv0al9vsgg3texae94xqdq8v3jhxccxqyjw5qcqp2p8r3c928hxsd94fsu9autr87uxs0p7ff4ykhxug8mt09s259uw6qcf4g64pn98zpzdxyms72sa0fmk48uhgs9reky7yge0f7lhh06kcpmzfwc6",
 *          "status": "complete",
 *          "payment_preimage": "1d7e34981f84e5c3d4fd3dfd5ef2e2fe81374e5be3802d009b2bfe65f5b8e6c3",
 *          "amount_sent_msat": "1000000msat"
 *       },
 */
@Data
public class PayInfo {
    private Optional<String> bolt11;

    @JsonProperty("payment_hash")
    private Optional<String> paymentHash;

    private Optional<String> destination;

    @JsonProperty("amount_msat")
    private Optional<String> amountMsat;

    private String status;

    @JsonProperty("payment_preimage")
    private String paymentPreimage;

    @JsonProperty("amount_sent_msat")
    private String amountSentMsat;
}
