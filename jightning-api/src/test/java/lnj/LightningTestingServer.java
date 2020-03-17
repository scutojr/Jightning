package lnj;

import clightning.LightningDaemon;
import clightning.apis.LightningClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.googlecode.jsonrpc4j.JsonRpcServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LightningTestingServer {
    private int port;
    private JsonRpcServer rpcServer;

    public LightningTestingServer() {
        try {
            Configuration conf = new Configuration();
            port = conf.getInt(Configuration.LIGHTNING_PROXY_PORT, 5556);

            LightningDaemon lightningDaemon = new LightningDaemon();
            LightningClient client = lightningDaemon.getLightningClient();
            rpcServer = new JsonRpcServer(
                    new ObjectMapper().registerModule(new Jdk8Module()),
                    client
            );
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start() throws IOException {
        Executor executor = Executors.newCachedThreadPool();
        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress("0.0.0.0", port));
        while (true) {
            try {
                Socket s = ss.accept();
                executor.execute(() -> {
                    while (true) {
                        try {
                            rpcServer.handleRequest(s.getInputStream(), s.getOutputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                            try {
                                s.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                break;
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        LightningTestingServer server = new LightningTestingServer();
        server.start();
    }
}
