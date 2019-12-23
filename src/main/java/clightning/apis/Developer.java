package clightning.apis;

public interface Developer {
    /**
     *dev-listaddrs [bip32_max_index]
     *    Show addresses list up to derivation {index} (default is the last bip32 index)
     */
    void devListAddrs();

    /**
     *dev-rescan-outputs
     *    Synchronize the state of our funds with bitcoind
     */
    void devRescanOutputs();
}
