package clightning.apis;

import clightning.RemoteException;
import clightning.apis.annotations.ParamTag;
import clightning.apis.optional.*;
import clightning.apis.response.*;

public interface BasedChannel {
    /**
     * attempts to close the channel cooperatively with the peer
     *
     * @param channelId If the given id is a peer ID (66 hex digits as a string), then it applies to the active channel
     *                  of the direct peer corresponding to the given peer ID. If the given id is a channel ID (64 hex
     *                  digits as a string, or the short channel ID blockheight:txindex:outindex form), then it applies
     *                  to that channel.
     * @return CloseResult
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-close.7.md>lightning-close</a>
     */
    CloseResult close(String channelId);

    /**
     * attempts to close the channel cooperatively with the peer, or unilaterally after unilateraltimeout,
     * and the to-local output will be sent to the address specified in destination.
     *
     * @param channelId      If the given id is a peer ID (66 hex digits as a string), then it applies to the active channel
     *                       of the direct peer corresponding to the given peer ID. If the given id is a channel ID (64 hex
     *                       digits as a string, or the short channel ID blockheight:txindex:outindex form), then it applies
     *                       to that channel.
     * @param optionalParams extra optional input parameters
     * @return CloseResult
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-close.7.md>lightning-close</a>
     */
    CloseResult close(@ParamTag(alias = "id") String channelId, CloseParams optionalParams);

    /**
     * It is a lower level RPC command. It allows channel funder to cancel a channel before funding broadcast with
     * a connected peer.
     *
     * @param id the node id of the remote peer with which to cancel
     * @return message about canceling operation
     * @throws RemoteException on failure, an error will be thrown
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-fundchannel_cancel.7.md>lightning-fundchannel_cancel</a>
     */
    String fundChannelCancel(String id);

    /**
     * It is a lower level RPC command. It allows channel funder to cancel a channel before funding broadcast with
     * a connected peer.
     *
     * @param id        the node id of the remote peer with which to cancel
     * @param channelId corresponding channel id
     * @return message about canceling operation
     * @throws RemoteException on failure, an error will be thrown
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-fundchannel_cancel.7.md>lightning-fundchannel_cancel</a>
     */
    String fundChannelCancel(String id, @ParamTag(optional = true) String channelId);

    /**
     * It is a lower level RPC command. It allows a user to complete an initiated channel establishment with a connected peer.
     *
     * @param id    the node id of the remote peer
     * @param txId  the hex string of the funding transaction id
     * @param txOut the integer outpoint of the funding output for this channel
     * @return FundChannelCompleteResult
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-fundchannel_complete.7.md>lightning-fundchannel_complete</a>
     */
    FundChannelCompleteResult fundChannelComplete(String id, String txId, int txOut);

    /**
     * It is a lower level RPC command. It allows a user to initiate channel establishment with a connected peer.
     *
     * @param id     the node id of the remote peer
     * @param amount the satoshi value that the channel will be funded at. This value MUST be accurate, otherwise
     *               the negotiated commitment transactions will not encompass the correct channel value
     * @return FundChannelStartResult
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-fundchannel_start.7.md>lightning-fundchannel_start</a>
     */
    FundChannelStartResult fundChannelStart(String id, long amount);

    /**
     * It is a lower level RPC command. It allows a user to initiate channel establishment with a connected peer.
     *
     * @param id             the node id of the remote peer
     * @param amount         the satoshi value that the channel will be funded at. This value MUST be accurate, otherwise
     *                       the negotiated commitment transactions will not encompass the correct channel value
     * @param optionalParams extra optional parameters
     * @return FundChannelStartResult
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-fundchannel_start.7.md>lightning-fundchannel_start</a>
     */
    FundChannelStartResult fundChannelStart(String id, long amount, FundChannelStartParams optionalParams);


    /**
     * Attempts to find the best route for the payment of msatoshi to lightning node id, such that the payment will
     * arrive at id with cltv-blocks to spare (default 9)
     * <p>
     * There are two considerations for how good a route is: how low the fees are, and how long your payment will
     * get stuck in a delayed output if a node goes down during the process. The {@code riskFactor} floating-point
     * field controls this trade off; it is the annual cost of your funds being stuck (as a percentage).
     *
     * @param id         lightning node id
     * @param msatoshi   amount of satoshi in millisatoshi precision
     * @param riskFactor control how good a route is
     * @return
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-getroute.7.md>lightning-getroute</a>
     */
    Route[] getRoute(String id, long msatoshi, double riskFactor);

    /**
     * Attempts to find the best route for the payment of msatoshi to lightning node id, such that the payment will
     * arrive at id with cltv-blocks to spare (default 9)
     * <p>
     * There are two considerations for how good a route is: how low the fees are, and how long your payment will
     * get stuck in a delayed output if a node goes down during the process. The {@code riskFactor} floating-point
     * field controls this trade off; it is the annual cost of your funds being stuck (as a percentage).
     *
     * @param id             lightning node id
     * @param msatoshi       amount of satoshi in millisatoshi precision
     * @param riskFactor     control how good a route is
     * @param optionalParams extra optional parameters
     * @return
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-getroute.7.md>lightning-getroute</a>
     */
    Route[] getRoute(String id, long msatoshi, double riskFactor, GetRouteParams optionalParams);

    /**
     * Returns data on channels that are known to the node. Because channels may be bidirectional, up to 2 objects
     * will be returned for each channel (one for each direction).
     *
     * @return an array of {@link Channel}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-listchannels.7.md>lightning-listchannels</a>
     */
    Channel[] listChannels();

    /**
     * Returns data on channels that are known to the node. Because channels may be bidirectional, up to 2 objects
     * will be returned for each channel (one for each direction).
     *
     * @param optionalParams extra optional parameters
     * @return an array of {@link Channel}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-listchannels.7.md>lightning-listchannels</a>
     */
    Channel[] listChannels(ListChannelsParams optionalParams);

    /**
     * Displays all htlcs that have been attempted to be forwarded by the c-lightning node
     *
     * @return an array of {@code Forward}
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-listforwards.7.md>lightning-listforwards</a>
     */
    Forward[] listForwards();

    /**
     * Set channel specific routing fees as defined in BOLT #7. The channel has to be in normal or awaiting state.
     * This can be checked by {@link BasedNetwork#listPeers} reporting a state of CHANNELD_NORMAL or
     * CHANNELD_AWAITING_LOCKIN for the channel.
     *
     * @param channelIdOrPeerId a scid (short channel ID), channel id or peerid (pubkey) of the channel to be modified.
     *                          If id is set to "all", the fees for all channels are updated that are in state
     *                          CHANNELD_NORMAL or CHANNELD_AWAITING_LOCKIN.
     * @return SetChannelFeeResult
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-setchannelfee.7.md>lightning-setchannelfee</a>
     */
    SetChannelFeeResult setChannelFee(String channelIdOrPeerId);

    /**
     * Set channel specific routing fees as defined in BOLT #7. The channel has to be in normal or awaiting state.
     * This can be checked by {@link BasedNetwork#listPeers} reporting a state of CHANNELD_NORMAL or
     * CHANNELD_AWAITING_LOCKIN for the channel.
     *
     * @param channelIdOrPeerId a scid (short channel ID), channel id or peerid (pubkey) of the channel to be modified.
     *                          If id is set to "all", the fees for all channels are updated that are in state
     *                          CHANNELD_NORMAL or CHANNELD_AWAITING_LOCKIN.
     * @param optionalParams    extra optional parameters
     * @return SetChannelFeeResult
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-setchannelfee.7.md>lightning-setchannelfee</a>
     */
    SetChannelFeeResult setChannelFee(@ParamTag(alias = "id") String channelIdOrPeerId, SetChannelFeeParams optionalParams);
}
