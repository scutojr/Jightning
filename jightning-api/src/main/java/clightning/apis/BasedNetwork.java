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
     * connect id [host] [port]
     * Connect to {id} at {host} (which can end in ':port' if not default). {id} can also be of the form id@host
     */
    String connect(String id);

    String connect(String id, String host);

    String connect(String id, @ParamTag(optional = true) String host, @ParamTag(optional = true) Integer port);

    /**
     * disconnect id [force]
     * Disconnect from {id} that has previously been connected to using connect; with {force} set, even if it has a current channel
     * <p>
     * lightning-cli disconnect 036c0793141c045a9e1e50efaa2740def367800580ecad7d31268103f9b9e97472 true
     * {}
     */
    void disconnect(String id);

    void disconnect(String id, @ParamTag(optional = true) boolean force);

    /**
     * listnodes [id]
     * Show node {id} (or all, if no {id}), in our local network view
     */
    Node[] listNodes();

    Node[] listNodes(@ParamTag(optional = true) String id);

    /**
     * listpeers [id] [level]
     * Show current peers, if {level} is set, include logs for {id}
     * <p>
     * Supplying id will filter the results to only return data on a node with a matching id, if one exists.
     * <p>
     * Supplying level will show log entries related to that peer at the given log level. Valid log levels are "io",
     * "debug", "info", and "unusual".
     */
    Peer[] listPeers();

    Peer[] listPeers(ListPeersParams optionalParams);

    /**
     * ping id [len] [pongbytes]
     * Send peer {id} a ping of length {len} (default 128) asking for {pongbytes} (default 128)
     */
    int ping(String id);

    int ping(String id, PingParams optionalParams);
}
