package clightning.apis.response;

import clightning.apis.BasedUtility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Response of {@link BasedUtility#getInfo}
 */
@Data
public class LightningDaemonInfo {
    private String id;
    private String alias;
    private String color;

    @JsonProperty("num_peers")
    private int numPeers;

    @JsonProperty("num_pending_channels")
    private int numPendingChannels;

    @JsonProperty("num_active_channels")
    private int numActiveChannels;

    @JsonProperty("num_inactive_channels")
    private int numInactiveChannels;

    private Address address[];
    private BindingWrapper binding[];
    private String version;
    private int blockheight;
    private String network;

    @JsonProperty("msatoshi_fees_collected")
    private int msatoshiFeesCollected;

    @JsonProperty("fees_collected_msat")
    private String feesCollectedMsat;

    @JsonProperty("warning_bitcoind_sync")
    private String warningBitcoindSync;

    @Data
    public static class Address {
        private String type;
        private String address;
        private int port;
    }

    /**
     * type="local socket",          socket
     * type="any protocol",          port
     * type="Tor generated address", service: Address
     * type="unresolved",            name, port
     * type="ipv4|ipv6|torv2|torv3", address, port
     */
    @Setter
    public static class BindingWrapper {
        @Getter
        private String type;

        private String socket;
        private int port;
        private Address service;
        private String name;
        private String address;

        public LocalSocket getLocalSocket() {
            Preconditions.checkState(
                    Type.LOCAL_SOCKET == Type.getType(type),
                    "type is not local socket"
            );
            return new LocalSocket();
        }

        public AnyProtocol getAnyProtocol() {
            Preconditions.checkState(
                    Type.ANY_PROTOCOL == Type.getType(type),
                    "type is not any protocol"
            );
            return new AnyProtocol();
        }

        public TorGeneratedAddress getTorGeneratedAddress() {
            Preconditions.checkState(
                    Type.TOR_GENERATED_ADDRESS == Type.getType(type),
                    "type is not Tor generated address"
            );
            return new TorGeneratedAddress();
        }

        public Unresolved getUnresolved() {
            Preconditions.checkState(
                    Type.UNRESOLVED == Type.getType(type),
                    "type is not unresolved"
            );
            return new Unresolved();
        }

        public IpOrTor getIpv4() {
            Preconditions.checkState(Type.IPV4 == Type.getType(type), "type is not ipv4");
            return new IpOrTor();
        }

        public IpOrTor getIpv6() {
            Preconditions.checkState(Type.IPV6 == Type.getType(type), "type is not ipv6");
            return new IpOrTor();
        }

        public IpOrTor getTorv2() {
            Preconditions.checkState(Type.TORV2 == Type.getType(type), "type is not torv2");
            return new IpOrTor();
        }

        public IpOrTor getTorv3() {
            Preconditions.checkState(Type.TORV3 == Type.getType(type), "type is not torv3");
            return new IpOrTor();
        }

        public class LocalSocket {
            public String getSocket() {
                return BindingWrapper.this.socket;
            }
        }

        public class AnyProtocol {
            public int getPort() {
                return BindingWrapper.this.port;
            }
        }

        public class TorGeneratedAddress {
            public Address getService() {
                return BindingWrapper.this.service;
            }
        }

        public class Unresolved {
            public String getName() {
                return BindingWrapper.this.name;
            }

            public int getPort() {
                return BindingWrapper.this.port;
            }
        }

        public class IpOrTor {
            public String getAddress() {
                return BindingWrapper.this.address;
            }

            public int getPort() {
                return BindingWrapper.this.port;
            }
        }

        public enum Type {
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

            /**
             * @param type
             * @return Type or null if no Type is not found for type
             */
            public static Type getType(String type) {
                return map.get(type);
            }
        }
    }
}
