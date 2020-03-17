package clightning.apis;

import clightning.apis.response.DevAddress;
import clightning.apis.response.DevRescanOutput;

import java.io.IOException;

public interface BasedDeveloper {
    /**
     * dev-listaddrs [bip32_max_index]
     * Show addresses list up to derivation {index} (default is the last bip32 index)
     *
     * @return
     */
    DevAddress[] devListAddrs(int bip32MaxIndex);

    /**
     * dev-rescan-outputs
     * Synchronize the state of our funds with bitcoind
     */
    DevRescanOutput[] devRescanOutputs();
}
