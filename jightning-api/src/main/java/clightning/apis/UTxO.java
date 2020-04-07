package clightning.apis;

/**
 * Unspent transaction output.
 */
public class UTxO {
    private String txId;
    private int vOut;

    public UTxO(String txId, int vOut) {
        this.txId = txId;
        this.vOut = vOut;
    }

    @Override
    public String toString() {
        return txId + ":" + vOut;
    }
}
