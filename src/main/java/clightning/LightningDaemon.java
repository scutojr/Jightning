package clightning;

import clightning.apis.LightningClient;
import clightning.apis.LightningClientImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.google.common.base.Preconditions.*;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.unix.DomainSocketAddress;

import java.io.*;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class LightningDaemon implements AbstractLightningDaemon {
    private static Map EMPTY_PARAMS = new HashMap();
    private AtomicInteger id = new AtomicInteger();
    private ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());

    private String udsPath;
    private EventLoopGroup group;
    private SocketAddress address;
    private Bootstrap bootstrap;

    public LightningDaemon() throws IOException {
        udsPath = getUnixDomainPath();
        group = new EpollEventLoopGroup();
        address = new DomainSocketAddress(udsPath);
        bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(EpollDomainSocketChannel.class)
                .remoteAddress(address);
    }

    private String getUnixDomainPath() throws IOException {
        Process p = Runtime.getRuntime().exec("lightning-cli listconfigs");
        JsonNode configs = mapper.readTree(p.getInputStream());
        JsonNode lightningDir = configs.get("lightning-dir");
        JsonNode rpcFile = configs.get("rpc-file");
        checkNotNull(lightningDir);
        checkNotNull(rpcFile);
        return String.format("%s/%s", lightningDir.asText(), rpcFile.asText());
    }

    private JsonNode createRequest(String method, Map params) {
        JsonNodeFactory nodeFactory = mapper.getNodeFactory();
        ObjectNode request = nodeFactory.objectNode();
        request.put("method", method);
        request.put("params", mapper.convertValue(params, JsonNode.class));
        request.put("id", id.incrementAndGet());
        return request;
    }

    /**
     * TODO: handle the RemoteException
     *
     * @param method
     * @param params
     * @param valueType
     * @param <T>
     * @return
     */
    @Override
    public synchronized <T> T execute(String method, Map params, Class<T> valueType) {
        try {
            byte[] req = mapper.writeValueAsBytes(createRequest(method, params));
            UdsConnection connection = new UdsConnection(req);
            Bootstrap bs = bootstrap.clone();
            bs.handler(new ChannelInitializer() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    channel.pipeline().addLast(connection);
                }
            }).connect();
            JsonNode rsp = mapper.readValue(connection.getResponse(), JsonNode.class);

            if (rsp.has("error")) {
                JsonNode error = rsp.get("error");
                int code = error.get("code").asInt();
                String message = error.get("message").asText();
                throw new RemoteException(message, code);
            }
            return mapper.treeToValue(rsp.get("result"), valueType);
        } catch (Exception e) {
            throw new RemoteException(e);
        }
    }

    @Override
    public synchronized <T> T execute(String method, Class<T> valueType) {
        return execute(method, EMPTY_PARAMS, valueType);
    }

    public LightningClient getLightningClient() {
        return new LightningClientImpl(this);
    }
}
