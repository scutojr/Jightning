package lnj.integration;

import clightning.AbstractLightningDaemon;
import clightning.LightningDaemon;
import clightning.apis.LightningClientImpl;
import clightning.apis.PluginCommand;
import clightning.apis.response.Peer;
import com.google.common.primitives.UnsignedInteger;
import lnj.Configuration;
import lnj.plugins.CustomPlugin;
import lnj.plugins.LightningPluginApi;
import lnj.utils.LightningUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TestBasedPluginModule {
    private static final String DELEGATOR = "plugin_delegator.py";
    private volatile CustomPlugin plugin;
    private PluginClient pluginClient;

    public static class PluginClient extends LightningClientImpl implements LightningPluginApi {
        public PluginClient(AbstractLightningDaemon lnd) {
            super(lnd);
        }

        @Override
        public int increaseBy(int by) {
            Map<String, Object> params = new HashMap<>();
            params.put("by", by);
            return lnd.execute("increaseBy", params, Integer.class);
        }
    }

    private void startPlugin() {
        Thread t = new Thread(() -> {
            Socket conn = null;
            ServerSocket ss = null;
            try {
                Configuration conf = new Configuration();
                int port = conf.getInt(Configuration.CUSTOM_PLUGIN_PORT, 33557);

                ss = new ServerSocket();
                ss.bind(new InetSocketAddress(port));
                conn = ss.accept();
                plugin = new CustomPlugin(conn.getInputStream(), conn.getOutputStream());
                plugin.addOption("option1", 100, "this is option1");
                plugin.addOption("option2", "default value", "this is option2");
                plugin.addOption("option3", true, "this is option3");

                plugin.logInfo("before running the plugin!");
                plugin.run(true);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (Objects.nonNull(conn)) {
                    try {
                        conn.close();
                    } catch (IOException e) {
                    }
                }
                if (Objects.nonNull(ss)) {
                    try {
                        ss.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {
        }
    }

    private void stopPlugin() {
        plugin.logInfo("stopping the plugin!");
        pluginClient.plugin(PluginCommand.stop(DELEGATOR));
        if (Objects.nonNull(plugin)) {
            plugin.stop();
        }
    }

    @Before
    public void setUp() throws IOException {
        startPlugin();

        URL url = TestBasedPluginModule.class.getClassLoader().getResource(DELEGATOR);
        pluginClient = new PluginClient(new LightningDaemon());
        pluginClient.plugin(PluginCommand.start(url.getPath()));
    }

    @After
    public void tearDown() {
        stopPlugin();
    }

    @Test
    public void testCommand() {
        plugin.logInfo("testing the plugin rpc command!");
        int initialValue = plugin.getValue();
        int increment = 5;
        int c = 10;
        for (int i = 1; i < c; i++) {
            pluginClient.increaseBy(increment);
            Assert.assertTrue(plugin.getValue() == initialValue + i * increment);
        }
    }

    @Test
    public void testSubscribeAndHook() throws InterruptedException {
        plugin.logInfo("testing the plugin subscription and hook!");

        Peer[] peers = pluginClient.listPeers();
        Assert.assertTrue(peers.length > 0);

        Peer peer = peers[0];
        String id = peer.getId();

        Assert.assertTrue(peer.isConnected());
        pluginClient.disconnect(id, true);
        peer = pluginClient.listPeers()[0];
        Assert.assertFalse(peer.isConnected());

        Assert.assertTrue(plugin.getHooks().size() == 0);
        pluginClient.connect(id, LightningUtils.getHost(id));
        Thread.sleep(4000);
        Assert.assertTrue(plugin.getHooks().size() > 0);
    }
}
