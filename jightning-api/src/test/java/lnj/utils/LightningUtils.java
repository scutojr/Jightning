package lnj.utils;

import clightning.LightningDaemon;
import clightning.apis.LightningClient;
import clightning.apis.response.Funds;
import clightning.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.googlecode.jsonrpc4j.JsonRpcClient;
import com.googlecode.jsonrpc4j.ProxyUtil;
import lnj.Configuration;
import lnj.integration.TestBasedPayment;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class LightningUtils {
    private static Map<String, String> idToHost;
    private static final Logger logger = LoggerFactory.getLogger(LightningUtils.class);

    static {
        String file = "nodeIdToHost.json";
        InputStream is = LightningUtils.class.getClassLoader().getResourceAsStream(file);
        try {
            idToHost = new ObjectMapper().readValue(is, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            logger.error("", e);
            System.exit(1);
        }
    }

    public interface Handler {
        void handle(LightningClient peer);
    }

    public static String getHost(String nodeId) {
        return idToHost.get(nodeId);
    }

    public static String[] getHosts() {
        Collection<String> hosts = idToHost.values();
        return hosts.toArray(new String[hosts.size()]);
    }

    public static LightningClient getPeer(Socket s) throws IOException {
        JsonRpcClient rpcClient = new JsonRpcClient();
        rpcClient.getObjectMapper().registerModule(new Jdk8Module());
        return ProxyUtil.createClientProxy(
                TestBasedPayment.class.getClassLoader(),
                LightningClient.class,
                rpcClient,
                s
        );
    }

    public static boolean waitForFundConfirmed() throws IOException {
        LightningClient client = new LightningDaemon().getLightningClient();
        boolean isConfirmed = false;
        ObjectMapper mapper = JsonUtil.getMapper();
        for (int i = 0; i < 32; i++) {
            Funds funds = client.listFunds();
            isConfirmed = Arrays.stream(funds.getOutputs()).anyMatch((output) -> {
                return output.getStatus().get().equals("confirmed");
            });
            if (isConfirmed) {
                break;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error("", e);
                }
            }
        }
        return isConfirmed;
    }

    public static void forEachPeer(Handler handler) {
        Socket s = null;
        try {
            Configuration conf = new Configuration();

            int port = conf.getInt(Configuration.LIGHTNING_PROXY_PORT, 5556);
            String hostname = CommonUtils.getHostname();
            String[] hosts = LightningUtils.getHosts();
            Assert.assertTrue(hosts.length > 0);

            for (String host : hosts) {
                if (hostname.equals(host)) {
                    continue;
                }
                s = new Socket(host, port);
                handler.handle(getPeer(s));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            if (Objects.nonNull(s)) {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
