package lnj;

import clightning.LightningDaemon;
import clightning.apis.LightningClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.googlecode.jsonrpc4j.JsonRpcServer;
import com.googlecode.jsonrpc4j.StreamServer;

import java.io.IOException;
import java.net.ServerSocket;

public class LightningTestingServer {
    private StreamServer server;

    public LightningTestingServer() {
        try {
            server = getServer();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public StreamServer getServer() throws IOException {
        LightningDaemon lightningDaemon = new LightningDaemon();
        LightningClient client = lightningDaemon.getLightningClient();
        JsonRpcServer server = new JsonRpcServer(
                new ObjectMapper().registerModule(new Jdk8Module()),
                client
        );

        int maxThreads = 1;
        // TODO: set the port by property
        int port = 5556;
        ServerSocket ss = new ServerSocket(port);
        StreamServer streamServer = new StreamServer(server, maxThreads, ss);
        streamServer.start();
        return streamServer;
    }

    public static void main(String[] args) {
        LightningTestingServer server = new LightningTestingServer();
    }
}
