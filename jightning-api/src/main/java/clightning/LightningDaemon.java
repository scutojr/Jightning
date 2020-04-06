package clightning;

import clightning.apis.LightningClient;
import clightning.apis.LightningClientImpl;
import clightning.apis.response.LightningDaemonInfo;
import clightning.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.google.common.base.Preconditions.*;

import com.google.common.util.concurrent.AbstractIdleService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.unix.DomainSocketAddress;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represent an lightning daemon for executing command.
 */
public class LightningDaemon extends AbstractIdleService implements AbstractLightningDaemon {
    private Logger logger = LoggerFactory.getLogger(LightningDaemon.class);

    private static Map EMPTY_PARAMS = new HashMap();
    private AtomicInteger id = new AtomicInteger();
    private ObjectMapper mapper;

    private Network net;
    private boolean tryStart;
    private String udsPath;
    private EventLoopGroup group;
    private SocketAddress address;
    private Bootstrap bootstrap;

    /**
     * Create an lightning daemon of test network
     */
    public LightningDaemon() {
        this(Network.testnet);
    }

    /**
     * Create an lightning daemon for @{code network}
     *
     * @param network network where lightning daemon will run
     */
    public LightningDaemon(Network network) {
        this(network, false);
    }

    /**
     * Create an lightning daemon for @{code network}. {@code tryStart} will determine whether start
     * the lightning daemon backend or not.
     *
     * @param network
     * @param tryStart
     */
    public LightningDaemon(Network network, boolean tryStart) {
        net = network;
        this.tryStart = tryStart;
    }

    @Override
    protected void startUp() throws Exception {
        startLightningDaemon();

        mapper = JsonUtil.getMapper();
        udsPath = getUnixDomainPath();
        group = new EpollEventLoopGroup();
        address = new DomainSocketAddress(udsPath);
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(EpollDomainSocketChannel.class)
                .remoteAddress(address);
    }

    @Override
    protected void shutDown() throws Exception {

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

    protected ObjectNode createRequest(String method, Map params) {
        ObjectNode request = mapper.createObjectNode();
        request.put("method", method);
        request.replace("params", mapper.convertValue(params, JsonNode.class));
        request.put("id", id.incrementAndGet());
        return request;
    }

    boolean isLndRunning() {
        LightningClient client = new LightningClientImpl(this);
        try {
            LightningDaemonInfo info = client.getInfo();
            if (!info.getNetwork().equals(net.name())) {
                String msg = String.format(
                        "inconsistent network type: expected %s, but get %s",
                        net.name(),
                        info.getNetwork()
                );
                logger.warn(msg);
            }
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    void startLightningDaemon() {
        if (!tryStart || isLndRunning()) {
            return;
        }
        String cmd = "lightningd --daemon --network=" + net;
        try {
            logger.info("starting lightning daemon: " + cmd);
            Process proc = Runtime.getRuntime().exec(cmd);
            proc.waitFor();
            if (proc.exitValue() == 0) {
                String stdout = IOUtils.toString(proc.getInputStream(), "utf-8");
                logger.info(stdout);
            } else {
                String errOut = IOUtils.toString(proc.getErrorStream(), "utf-8");
                logger.error(errOut);
            }
        } catch (IOException e) {
            logger.error("failed to execute command: " + cmd, e);
        } catch (InterruptedException e) {
            String msg = String.format("unexpected interruption from waiting for command \"%s\"to finish", cmd);
            logger.error(msg, e);
        }
    }

    /**
     * Execute lightning daemon command
     *
     * @param method    command name
     * @param params    params required to execute the command
     * @param valueType the return value type
     * @param <T>       the return value type
     * @return command response
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

    /**
     * Execute lightning daemon command
     *
     * @param method    command name
     * @param valueType the return value type
     * @param <T>       the return value type
     * @return command response
     */
    @Override
    public synchronized <T> T execute(String method, Class<T> valueType) {
        return execute(method, EMPTY_PARAMS, valueType);
    }
}
