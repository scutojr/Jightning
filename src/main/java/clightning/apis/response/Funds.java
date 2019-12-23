package clightning.apis.response;

import lombok.Data;

@Data
public class Funds {
    private TxOutput[] outputs;
    private Channel[] channels;
}
