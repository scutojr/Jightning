package lnj.unit;

import clightning.plugin.*;
import clightning.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.googlecode.jsonrpc4j.JsonRpcClient;
import lombok.Data;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

public class TestBasedPluginModule {
    private ObjectMapper mapper;
    private Thread thread;
    private ChannelMgr channelMgr;
    private PipedInputStream forwardIn;
    private PipedOutputStream forwardOut;

    private PipedInputStream backwardIn;
    private PipedOutputStream backwardOut;

    @Before
    public void setUp() throws IOException {
        mapper = JsonUtil.getMapper();

        forwardIn = new PipedInputStream();
        forwardOut = new PipedOutputStream(forwardIn);

        backwardIn = new PipedInputStream();
        backwardOut = new PipedOutputStream(backwardIn);

        channelMgr = new ChannelMgr(forwardIn, backwardOut);
        thread = new Thread(() -> {
            try {
                channelMgr.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @After
    public void tearDown() throws IOException, InterruptedException {
        forwardOut.close();
        backwardOut.close();
        thread.interrupt();
        thread.join();
    }

    private ObjectNode rawRequest(String method, JsonNode params, boolean withId) {
        ObjectNode request = new ObjectMapper().createObjectNode();
        request.put("method", method);
        request.put("jsonrpc", "2.0");
        request.replace("params", params);
        if (withId) {
            request.put("id", System.currentTimeMillis());
        }
        return request;
    }

    @Test
    public void testKeyWordArgsRequest() throws IOException, InterruptedException {
        String id = "<peer id>";
        String host = "xx host";
        long defSatoshi = 15000000;
        int defFee = 100;

        ObjectNode params = mapper.createObjectNode();
        params.put("id", id);
        params.put("host", host);
        JsonNode request = rawRequest("createchannel", params, true);
        mapper.writeValue(forwardOut, request);

        Thread.sleep(2 * 1000);
        Assert.assertTrue(channelMgr.getId().equals(id));
        Assert.assertTrue(channelMgr.getSatoshi() == defSatoshi);
        Assert.assertTrue(channelMgr.getFee() == defFee);
    }

    @Test
    public void testPlugin() throws Throwable {
        // JsonRpcClient will create a request with params of type ArrayNode
        JsonRpcClient client = new JsonRpcClient();

        // test getManifest()
        JsonNode manifest = client.invokeAndReadResponse(
                "getmanifest",
                mapper.createArrayNode(),
                JsonNode.class,
                forwardOut,
                backwardIn
        );
        Assert.assertNotNull(manifest);
        Assert.assertTrue(manifest.get("subscriptions").size() > 0);
        Assert.assertTrue(manifest.get("hooks").size() > 0);
        Assert.assertTrue(manifest.get("rpcmethods").size() > 0);

        // test createChannel
        String id = "<peer id>";
        long satoshi = 10 * 1000 * 1000;
        int defFee = 100;
        client.invoke(
                "createchannel",
                mapper.createArrayNode().add(id).add("host").add(satoshi),
                forwardOut
        );
        Thread.sleep(1000);
        synchronized (channelMgr) {
            Assert.assertTrue(channelMgr.id.equals(id));
            Assert.assertTrue(channelMgr.satoshi == satoshi);
            Assert.assertTrue(channelMgr.fee == defFee);
        }


        // test logConnect
        client.invoke(
                "logConnect",
                mapper.createObjectNode().putPOJO("info", new ConnectInfo("the id", "the address")),
                forwardOut
        );
        Thread.sleep(1000);
        synchronized (channelMgr) {
            Assert.assertTrue(channelMgr.isLogConnect);
        }

        // test connect notification
        mapper.writeValue(
                forwardOut,
                rawRequest(
                        "connect",
                        mapper.convertValue(new ConnectInfo("the id", "the address"), JsonNode.class),
                        false
                )
        );
        Thread.sleep(1000);
        synchronized (channelMgr) {
            Assert.assertTrue(channelMgr.isConnected);
        }

        // test invoice_payment notification
        ObjectNode p = mapper.createObjectNode();
        p.replace("invoice_payment", mapper.createObjectNode());
        mapper.writeValue(
                forwardOut,
                rawRequest(
                        "invoice_payment",
                        p, // a object based request
                        false
                )
        );
        Thread.sleep(1000);
        synchronized (channelMgr) {
            Assert.assertTrue(channelMgr.isPaid);
        }

        // test peer_connected hook
        p = mapper.createObjectNode();
        p.replace("peer", mapper.createObjectNode());
        JsonNode r = client.invokeAndReadResponse(
                "peer_connected",
                p,
                JsonNode.class,
                forwardOut,
                backwardIn
        );
        Assert.assertTrue(r.has("result") && r.has("error_message"));
        forwardOut.close();
        backwardOut.close();
    }

    @Data
    public static class ConnectInfo {
        private String id;
        private String address;

        public ConnectInfo() {
        }

        public ConnectInfo(String id, String address) {
            this.id = id;
            this.address = address;
        }
    }


    public static class ChannelMgr extends Plugin {
        private String id;
        private long satoshi;
        private int fee;
        private boolean isConnected = false;
        private boolean isLogConnect = false;
        private boolean isPaid = false;
        private boolean isPeerConnectedHandled = false;

        private InputStream in;
        private OutputStream out;

        public ChannelMgr(InputStream in, OutputStream out) {
            super(in, out);
            this.in = in;
            this.out = out;
        }


        /**
         * @return arbitrary data to tell the lightning daemon that this plugin is ready
         */
        @Override
        protected Object initialize(JsonNode options, JsonNode configuration) {
            return "arbitray data that can be serialized by jsonrpc4j";
        }

        public void stop() {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Command(name = "createchannel")
        public synchronized void createChannel(String id, String host, @DefaultTo("15000000") long satoshi, @DefaultTo("100") int fee) {
            this.id = id;
            this.satoshi = satoshi;
            this.fee = fee;
        }

        @Command
        public synchronized void logConnect(ConnectInfo info) {
            isLogConnect = true;
        }

        @Subscribe(NotificationTopic.connect)
        public synchronized void onConnect(String id, String address) {
            isConnected = true;
        }

        @Subscribe(NotificationTopic.invoice_payment)
        public synchronized void onInvoicePaid(JsonNode invoice_payment) {
            isPaid = true;
        }

        @Hook(HookTopic.peer_connected)
        public synchronized Object peerConnectedHook(JsonNode peer) {
            isPeerConnectedHandled = true;
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode response = mapper.createObjectNode();
            response.put("result", "disconnect");
            response.put("error_message", "this is an error message");
            return response;
        }

        public synchronized String getId() {
            return id;
        }

        public synchronized long getSatoshi() {
            return satoshi;
        }

        public synchronized int getFee() {
            return fee;
        }
    }
}
