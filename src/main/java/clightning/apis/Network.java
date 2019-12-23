package clightning.apis;

import java.io.IOException;

public interface Network {
    /**
     * TODO: make sure the type of port, string or int?
     *connect id [host] [port]
     *    Connect to {id} at {host} (which can end in ':port' if not default). {id} can also be of the form id@host
     */
    String connect(String id, String host) throws IOException;

    String connect(String id, String host, Integer port) throws IOException;

    /**
     *disconnect id [force]
     *    Disconnect from {id} that has previously been connected to using connect; with {force} set, even if it has a current channel
     */
    void disconnect();

    /**
     *listnodes [id]
     *    Show node {id} (or all, if no {id}), in our local network view
     */
    void listNodes();

    /**
     *listpeers [id] [level]
     *    Show current peers, if {level} is set, include logs for {id}
     */
    void listPeers();

    /**
     *ping id [len] [pongbytes]
     *    Send peer {id} a ping of length {len} (default 128) asking for {pongbytes} (default 128)
     */
    void ping();
}
