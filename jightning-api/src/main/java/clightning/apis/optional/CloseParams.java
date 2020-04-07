package clightning.apis.optional;

import clightning.apis.BasedPlugin;

/**
 * Extra optional parameters for {@link clightning.apis.BasedChannel#close}
 */
public class CloseParams extends OptionalParams {
    /**
     * set the unilateraltimeout.If unilateraltimeout is not zero, the close command will unilaterally close the
     * channel when that number of seconds is reached. If unilateraltimeout is zero, then the close command will
     * wait indefinitely until the peer is online and can negotiate a mutual close. The default is 2 days (172800
     * seconds).
     *
     * @param unilateralTimeoutSec the unilateralTimeout in second
     * @return object being called
     */
    public CloseParams setUnilateralTimeout(long unilateralTimeoutSec) {
        params.put("unilateraltimeout", unilateralTimeoutSec);
        return this;
    }

    /**
     * The destination can be of any Bitcoin accepted type, including bech32. If it isn't specified, the default
     * is a c-lightning wallet address.
     *
     * @param destAttr destination address
     * @return object being called
     */
    public CloseParams setDestination(String destAttr) {
        params.put("destination", destAttr);
        return this;
    }
}
