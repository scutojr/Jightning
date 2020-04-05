package clightning.apis;

import clightning.apis.annotations.ParamTag;
import clightning.apis.response.DevAddress;
import clightning.apis.response.DevRescanOutput;

public interface BasedDeveloper {
    /**
     * Show addresses list up to derivation {index} (default is the last bip32 index)
     *
     * @return an arrary of {@code DevAddress}
     */
    DevAddress[] devListAddrs();

    /**
     * Show addresses list up to derivation {index} (default is the last bip32 index)
     *
     * @return an arrary of {@code DevAddress}
     */
    DevAddress[] devListAddrs(@ParamTag(optional = true) int bip32MaxIndex);

    /**
     * Synchronize the state of our funds with bitcoind
     *
     * @return an array of {@code DevRescanOutput}
     */
    DevRescanOutput[] devRescanOutputs();
}
