package clightning;

/**
 * Enumeration for network type
 */
public enum Network {
    bitcoin,
    testnet,
    regtest,
    litecoin,
    litecoin_testnet;

    public String net() {
        if (this == litecoin_testnet) {
            return name().replace('_', '-');
        } else {
            return name();
        }
    }
}
