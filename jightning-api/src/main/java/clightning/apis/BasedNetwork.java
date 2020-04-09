package clightning.apis;

import clightning.apis.annotations.ParamTag;
import clightning.apis.optional.ListPeersParams;
import clightning.apis.optional.PingParams;
import clightning.apis.response.LightningDaemonInfo;
import clightning.apis.response.Node;
import clightning.apis.response.Peer;

import java.io.IOException;

public interface BasedNetwork {

    /**
     * Establishes a new connection with another node in the Lightning Network
     *
     * @param id represents the target node's public key. As a convenience, id may be of the form id@host or
     *           id@host:port. In this case, the host and port parameters must be omitted.
     * @return the peer id
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-connect.7.md>lightning-connect</a>
     */
    String connect(String id);

    /**
     * Establishes a new connection with another node in the Lightning Network
     *
     * @param id   represents the target node's public key. As a convenience, id may be of the form id@host or
     *             id@host:port. In this case, the host and port parameters must be omitted.
     * @param host the peer's hostname or IP address
     * @return the peer id
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-connect.7.md>lightning-connect</a>
     */
    String connect(String id, String host);

    /**
     * Establishes a new connection with another node in the Lightning Network
     *
     * @param id   represents the target node's public key. As a convenience, id may be of the form id@host or
     *             id@host:port. In this case, the host and port parameters must be omitted.
     * @param host the peer's hostname or IP address
     * @param port the peer's port, default to 9735
     * @return the peer id
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-connect.7.md>lightning-connect</a>
     */
    String connect(String id, @ParamTag(optional = true) String host, @ParamTag(optional = true) Integer port);

    /**
     * Closes an existing connection to a peer, identified by id, in the Lightning Network, as long as it doesn't have
     * an active channel.
     *
     * @param id the peer id
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-disconnect.7.md>lightning-disconnect</a>
     */
    void disconnect(String id);

    /**
     * Closes an existing connection to a peer, identified by id, in the Lightning Network, as long as it doesn't have
     * an active channel. If force is set then it will disconnect even with an active channel.
     *
     * @param id    the peer id
     * @param force if it is set, then it will disconnect even with an active channel
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-disconnect.7.md>lightning-disconnect</a>
     */
    void disconnect(String id, @ParamTag(optional = true) boolean force);

    /**
     * Show node {id} (or all, if no {id}), in our local network view
     *
     * @return an array of {@code Node}
     */
    Node[] listNodes();

    /**
     * Show node {id} (or all, if no {id}), in our local network view
     *
     * @param id node id
     * @return an array of {@code Node}
     */
    Node[] listNodes(@ParamTag(optional = true) String id);

    Peer[] listPeers();

    /**
     * @param optionalParams
     * @return
     * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/lightning-listpeers.7.md>lightning-listpeers</a>
     */
    Peer[] listPeers(ListPeersParams optionalParams);

    /**
     * Send peer {id} a ping of length {len} (default 128) asking for {pongbytes} (default 128)
     *
     * @param id peer node id
     * @return totlen
     */
    int ping(String id);

    /**
     * Send peer {id} a ping of length {len} (default 128) asking for {pongbytes} (default 128)
     *
     * @param id             peer node id
     * @param optionalParams extra optional parameters
     * @return totlen
     */
    int ping(String id, PingParams optionalParams);
}
