package clightning.apis.optional;

import com.google.common.base.Preconditions;

public class GetRouteParams extends OptionalParams {
    /**
     * The getroute RPC command attempts to find the best route for the payment of msatoshi to
     * lightning node id, such that the payment will arrive at id with cltv-blocks to spare (default 9).
     * @param cltv maximum number of block to spare for payment to arrive the destination
     * @return
     */
    public GetRouteParams setCltv(int cltv) {
        params.put("cltv", cltv);
        return this;
    }

    /**
     * @param fromId  the node to start the route from: default is this node
     * @return
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
     * @return
     */
    public GetRouteParams setFuzzPercent(double fuzzPercent) {
        Preconditions.checkState(fuzzPercent > 0);
        params.put("fuzzpercent", fuzzPercent);
        return this;
    }

    /**
     * exclude is a JSON array of short-channel-id/direction (e.g. ["564334x877x1/0", "564195x1292x0/1"]) or
     * node-id which should be excluded from consideration for routing. The default is not to exclude any channels
     * or nodes. Note if the source or destination is excluded, the command result is undefined.
     *
     * 0 == from lesser id node, 1 == to lesser id node
     *
     * @param exclude
     * @return
     */
    public GetRouteParams setExclude(String[] exclude) {
        params.put("exclude", exclude);
        return this;
    }

    /**
     * @param maxHops maxhops is the maximum number of channels to return; default is 20
     * @return
     */
    public GetRouteParams setMaxHops(int maxHops) {
        params.put("maxhops", maxHops);
        return this;
    }
}
