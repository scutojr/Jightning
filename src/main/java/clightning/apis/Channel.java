package clightning.apis;

import clightning.apis.optional.*;
import clightning.apis.response.*;

import java.io.IOException;
import java.util.List;

public interface Channel {
    /**
     * https://lightning.readthedocs.io/lightning-close.7.html
     * <p>
     * close id [unilateraltimeout] [destination]
     * Close the channel with {id} (either peer ID, channel ID, or short channel ID). Force a unilateral close after {unilateraltimeout} seconds (default 48h). If {destination} address is provided, will be used as output address.
     * <p>
     * id: then it applies to the active channel of the direct peer corresponding to the given
     * peer ID. If the given id is a channel ID (64 hex digits as a string, or the short channel
     * ID blockheight:txindex:outindex form), then it applies to that channel
     */
    CloseResult close(String channelId) throws IOException;
    CloseResult close(String channelId, CloseParams optionalParams) throws IOException;

    /**
     * fundchannel_cancel id [channel_id]
     * Cancel inflight channel establishment with peer {id}.
     */
    String fundChannelCancel(String id) throws IOException;

    /**
     * https://lightning.readthedocs.io/lightning-fundchannel_complete.7.html
     *
     * fundchannel_complete id txid txout
     * Complete channel establishment with peer {id} for funding transactionwith {txid}. Returns true on success, false otherwise.
     *
     * id is the node id of the remote peer.
     *
     * txid is the hex string of the funding transaction id.
     *
     * txout is the integer outpoint of the funding output for this channel.
     */
    FundChannelCompleteResult fundChannelComplete(String id, String txId, int txOut) throws IOException;

    /**
     * https://lightning.readthedocs.io/lightning-fundchannel_start.7.html
     *
     * fundchannel_start id amount [feerate] [announce] [close_to]
     * Start fund channel with {id} using {amount} satoshis. Returns a bech32 address to use as an output for a funding transaction.
     */
    FundChannelStartResult fundChannelStart(String id, long amount) throws IOException;

    FundChannelStartResult fundChannelStart(String id, long amount, FundChannelStartParams optionalParams) throws IOException;

    /**
     * getroute id msatoshi riskfactor [cltv] [fromid] [fuzzpercent] [exclude] [maxhops]
     * Show route to {id} for {msatoshi}, using {riskfactor} and optional {cltv} (default 9). If specified search from {fromid} otherwise use this node as source. Randomize the route with up to {fuzzpercent} (default 5.0). {exclude} an array of short-channel-id/direction (e.g. [ '564334x877x1/0', '564195x1292x0/1' ]) or node-id from consideration. Set the {maxhops} the route can take (default 20).
     */
    Route[] getRoute(String id, long msatoshi, double riskFactor) throws IOException;

    Route[] getRoute(String id, long msatoshi, double riskFactor, GetRouteParams optionalParams) throws IOException;

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
    List<clightning.apis.response.Channel> listChannels() throws IOException;

    List<clightning.apis.response.Channel> listChannels(ListChannelsParams optionalParams) throws IOException;

    /**
     * listforwards
     * List all forwarded payments and their information
     */
    void listForwards();

    /**
     * https://lightning.readthedocs.io/lightning-setchannelfee.7.html
     *
     * setchannelfee id [base] [ppm]
     * Sets specific routing fees for channel with {id} (either peer ID, channel ID, short channel ID or 'all'). Routing fees are defined by a fixed {base} (msat) and a {ppm} (proportional per millionth) value. If values for {base} or {ppm} are left out, defaults will be used. {base} can also be defined in other units, for example '1sat'. If {id} is 'all', the fees will be applied for all channels.
     *
     * id is required and should contain a scid (short channel ID), channel id or peerid (pubkey) of the channel
     * to be modified. If id is set to “all”, the fees for all channels are updated that are in state CHANNELD_NORMAL
     * or CHANNELD_AWAITING_LOCKIN.
     */
    SetChannelFeeResult setChannelFee(String channelIdOrPeerId) throws IOException;

    SetChannelFeeResult setChannelFee(String channelIdOrPeerId, SetChannelFeeParams optionalParams) throws IOException;
}
