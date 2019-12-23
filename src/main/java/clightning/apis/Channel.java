package clightning.apis;

public interface Channel {
    /**
     * https://lightning.readthedocs.io/lightning-close.7.html
     * <p>
     * close id [unilateraltimeout] [destination]
     * Close the channel with {id} (either peer ID, channel ID, or short channel ID). Force a unilateral close after {unilateraltimeout} seconds (default 48h). If {destination} address is provided, will be used as output address.
     * <p>
     * id: then it applies to the active channel of the direct peer corresponding to the given
     * peer ID. If the given id is a channel ID (64 hex digits as a string, or the short channel
     * ID blockheight:txindex:outindex form), then it applies to that channel.
     * <p>
     * unilateraltimeout: If unilateraltimeout is not zero, the close command will unilaterally close the channel
     * when that number of seconds is reached. If unilateraltimeout is zero, then the close
     * command will wait indefinitely until the peer is online and can negotiate a mutual close.
     * The default is 2 days (172800 seconds).
     * <p>
     * The destination can be of any Bitcoin accepted type, including bech32. If it isn’t specified,
     * the default is a c-lightning wallet address.
     */
    void close();

    /**
     * fundchannel_cancel id [channel_id]
     * Cancel inflight channel establishment with peer {id}.
     */
    void fundChannelCancel();

    /**
     * fundchannel_complete id txid txout
     * Complete channel establishment with peer {id} for funding transactionwith {txid}. Returns true on success, false otherwise.
     */
    void fundChannelComplete();

    /**
     * fundchannel_start id amount [feerate] [announce] [close_to]
     * Start fund channel with {id} using {amount} satoshis. Returns a bech32 address to use as an output for a funding transaction.
     */
    void fundChannelStart();

    /**
     * getroute id msatoshi riskfactor [cltv] [fromid] [fuzzpercent] [exclude] [maxhops]
     * Show route to {id} for {msatoshi}, using {riskfactor} and optional {cltv} (default 9). If specified search from {fromid} otherwise use this node as source. Randomize the route with up to {fuzzpercent} (default 5.0). {exclude} an array of short-channel-id/direction (e.g. [ '564334x877x1/0', '564195x1292x0/1' ]) or node-id from consideration. Set the {maxhops} the route can take (default 20).
     */
    void getRoute();

    /**
     * https://lightning.readthedocs.io/lightning-listchannels.7.html
     *
     * listchannels [short_channel_id] [source]
     * Show channel {short_channel_id} or {source} (or all known channels, if not specified)
     *
     * short_channel_id: If short_channel_id is supplied, then only known channels with a
     *     matching short_channel_id are returned.
     * source: If source is supplied, then only channels leading from that node id are returned.
     */
    void listChannels();

    /**
     * listforwards
     * List all forwarded payments and their information
     */
    void listForwards();

    /**
     * setchannelfee id [base] [ppm]
     * Sets specific routing fees for channel with {id} (either peer ID, channel ID, short channel ID or 'all'). Routing fees are defined by a fixed {base} (msat) and a {ppm} (proportional per millionth) value. If values for {base} or {ppm} are left out, defaults will be used. {base} can also be defined in other units, for example '1sat'. If {id} is 'all', the fees will be applied for all channels.
     */
    void setChannelFee();
}
