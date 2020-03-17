package lnj.plugins;

import clightning.plugin.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lnj.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CustomPlugin extends Plugin implements LightningPluginApi {
    private int value = 0;
    private volatile boolean running = false;
    private List<JsonNode> connections = new ArrayList<>();
    private List<JsonNode> hooks = new ArrayList<>();

    public CustomPlugin(InputStream in, OutputStream out) {
        super(in, out);
    }

    @Override
    protected Object initialize(JsonNode options, JsonNode configuration) {
        return "arbitrary data";
    }

    @Command
    @Override
    public synchronized int increaseBy(@DefaultTo("1") int by) {
        value += by;
        return value;
    }

    @Subscribe(NotificationTopic.connect)
    public synchronized void handleConnect(String id, String address) {
        ObjectNode rsp = mapper.createObjectNode();
        rsp.put("id", id);
        rsp.put("address", address);
        connections.add(rsp);
    }

    @Hook(HookTopic.peer_connected)
    public synchronized JsonNode preHandlePeerConnected(JsonNode peer) {
        hooks.add(peer);
        ObjectNode result = mapper.createObjectNode();
        result.put("result", "continue");
        return result;
    }

    public synchronized List<JsonNode> getConnections() {
        return connections;
    }

    public synchronized int getValue() {
        return value;
    }

    public synchronized List<JsonNode> getHooks() {
        return hooks;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void stop() {
        running = false;
    }

    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        int port = Integer.valueOf(conf.get(Configuration.CUSTOM_PLUGIN_PORT));

        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress(port));
        Socket conn = ss.accept();

        CustomPlugin plugin = new CustomPlugin(conn.getInputStream(), conn.getOutputStream());
        plugin.addOption("option1", 100, "this is option1");
        plugin.addOption("option2", "default value", "this is option2");
        plugin.run(true);
    }
}
