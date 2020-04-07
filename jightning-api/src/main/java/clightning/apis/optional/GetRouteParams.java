package clightning.apis.optional;

import com.google.common.base.Preconditions;

/**
 * Extra optional parameters for {@link clightning.apis.BasedChannel#getRoute(String, long, double, GetRouteParams)}
 */
public class GetRouteParams extends OptionalParams {
    /**
     * set the cltv
     *
     * @param cltv cltv-blocks to spare
     * @return object being called
     */
    public GetRouteParams setCltv(int cltv) {
        params.put("cltv", cltv);
        return this;
    }

    /**
     * set from id
     *
     * @param fromId the node to start the route from: default is this node
     * @return object being called
     */
    public GetRouteParams setFromId(String fromId) {
        params.put("fromid", fromId);
        return this;
    }

    /**
     * The fuzzpercent is used to distort computed fees along each channel, to provide some randomization
     * to the route generated. 0.0 means the exact fee of that channel is used, while 100.0 means the fee
     * used might be from 0 to twice the actual fee. The default is 5.0, or up to 5% fee distortion.
     *
     * @param fuzzPercent a positive floating-point number, representing a percentage of the actual fee
     * @return object being called
     */
    public GetRouteParams setFuzzPercent(double fuzzPercent) {
        Preconditions.checkState(fuzzPercent > 0);
        params.put("fuzzpercent", fuzzPercent);
        return this;
    }

    /**
     * exclude is an array of short-channel-id/direction (e.g. ["564334x877x1/0", "564195x1292x0/1"]) or
     * node-id which should be excluded from consideration for routing. The default is not to exclude any channels
     * or nodes. Note if the source or destination is excluded, the command result is undefined.
     *
     * @param exclude an array of short-channel-id/direction
     * @return object being called
     */
    public GetRouteParams setExclude(String[] exclude) {
        params.put("exclude", exclude);
        return this;
    }

    /**
     * set maxhops
     *
     * @param maxHops maxhops is the maximum number of channels to return; default is 20
     * @return object being called
     */
    public GetRouteParams setMaxHops(int maxHops) {
        params.put("maxhops", maxHops);
        return this;
    }
}
