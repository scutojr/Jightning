package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

/**
 *{
 *   "txid": "9790451a10359356138ba005632636a42d2f39cb3c32802d14eb028994965033",
 *   "output": 0,
 *   "value": 1000000000,
 *   "amount_msat": "1000000000000msat",
 *   "address": "bcrt1qlcfh4mjqtelj04a9r874pypj7lxd4z59z66u7c",
 *   "status": "confirmed",
 *   "blockheight": 102
 *}
 * TODO: check the json definition for Output in c-lightning code
 */
@Data
public class TxOutput {

    @JsonSetter("txid")
    private String txId;

    private int output;
    private long value;

    @JsonSetter("amount_msat")
    private String amountMsat;

    private String address;
    private String status;

    @JsonSetter("blockheight")
    private int blockHeight;
}
