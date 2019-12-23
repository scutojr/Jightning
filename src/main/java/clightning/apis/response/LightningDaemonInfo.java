package clightning.apis.response;


import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.HashMap;
import java.util.Map;

public class LightningDaemonInfo {
    private String id;
    private String alias;
    private String color;

    @JsonSetter("num_peers")
    private int numPeers;

    @JsonSetter("num_pending_channels")
    private int numPendingChannels;

    @JsonSetter("num_active_channels")
    private int numActiveChannels;

    @JsonSetter("num_inactive_channels")
    private int numInactiveChannels;

    private Address address[]; // what is the datatype?
    private Binding binding[]; // what is the datatype?
    private String version;
    private int blockheight;
    private String network;

    @JsonSetter("msatoshi_fees_collected")
    private int msatoshiFeesCollected;

    @JsonSetter("fees_collected_msat")
    private String feesCollectedMsat;

    @JsonSetter("warning_bitcoind_sync")
    private String warningBitcoindSync;

    /**
     * {
     *    "address": [],
     *    "binding": [
     *       {
     *          "type": "ipv6",
     *          "address": "::",
     *          "port": 9735
     *       },
     *       {
     *          "type": "ipv4",
     *          "address": "0.0.0.0",
     *          "port": 9735
     *       }
     *    ],
     * }
     */

    public static class Address {
        private String type;
        private String address;
        private int port;

        public String getType() {
            return type;
        }

        public String getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public static class Binding extends Address{
        /**
         * type="local socket", socket
         * type="any protocol", port
         * type="Tor generated address", service: Address
         * type="unresolved", name, port
         * type="ipv4|ipv6|torv2|torv3", address, port
         */

        private String type;
        private String socket;
        private Address service;
        private String name;

        enum Type {
            LOCAL_SOCKET("local socket"),
            ANY_PROTOCOL("any protocol"),
            TOR_GENERATED_ADDRESS("Tor generated address"),
            UNRESOLVED("unresolved"),
            IPV4("ipv4"),
            IPV6("ipv6"),
            TORV2("torv2"),
            TORV3("torv3");

            private static Map<String, Type> map = new HashMap<String, Type>();
            static {
                map.put("local socket", LOCAL_SOCKET);
                map.put("any protocol", ANY_PROTOCOL);
                map.put("Tor generated address", TOR_GENERATED_ADDRESS);
                map.put("unresolved", UNRESOLVED);
                map.put("ipv4", IPV4);
                map.put("ipv6", IPV6);
                map.put("torv2", TORV2);
                map.put("torv3", TORV3);
            }

            private String type;

            Type(String type) {
                this.type = type;
            }
            public static Type getType(String type) {
                // TODO: deal with case of key error
                return map.get(type);
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    public String getColor() {
        return color;
    }

    public int getNumPeers() {
        return numPeers;
    }

    public int getNumPendingChannels() {
        return numPendingChannels;
    }

    public int getNumActiveChannels() {
        return numActiveChannels;
    }

    public int getNumInactiveChannels() {
        return numInactiveChannels;
    }

    public Address[] getAddress() {
        return address;
    }

    public Binding[] getBinding() {
        return binding;
    }

    public String getVersion() {
        return version;
    }

    public int getBlockheight() {
        return blockheight;
    }

    public String getNetwork() {
        return network;
    }

    public int getMsatoshiFeesCollected() {
        return msatoshiFeesCollected;
    }

    public String getFeesCollectedMsat() {
        return feesCollectedMsat;
    }

    public String getWarningBitcoindSync() {
        return warningBitcoindSync;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setNumPeers(int numPeers) {
        this.numPeers = numPeers;
    }

    public void setNumPendingChannels(int numPendingChannels) {
        this.numPendingChannels = numPendingChannels;
    }

    public void setNumActiveChannels(int numActiveChannels) {
        this.numActiveChannels = numActiveChannels;
    }

    public void setNumInactiveChannels(int numInactiveChannels) {
        this.numInactiveChannels = numInactiveChannels;
    }

    public void setAddress(Address[] address) {
        this.address = address;
    }

    public void setBinding(Binding[] binding) {
        this.binding = binding;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setBlockheight(int blockheight) {
        this.blockheight = blockheight;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public void setMsatoshiFeesCollected(int msatoshiFeesCollected) {
        this.msatoshiFeesCollected = msatoshiFeesCollected;
    }

    public void setFeesCollectedMsat(String feesCollectedMsat) {
        this.feesCollectedMsat = feesCollectedMsat;
    }

    public void setWarningBitcoindSync(String warningBitcoindSync) {
        this.warningBitcoindSync = warningBitcoindSync;
    }
}
