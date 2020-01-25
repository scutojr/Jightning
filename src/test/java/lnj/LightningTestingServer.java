package lnj.runner;

import clightning.LightningDaemon;
import clightning.apis.LightningClient;
import com.googlecode.jsonrpc4j.JsonRpcClient;
import com.googlecode.jsonrpc4j.JsonRpcServer;
import com.googlecode.jsonrpc4j.ProxyUtil;
import com.googlecode.jsonrpc4j.StreamServer;
import lnj.PaymentService;
import lnj.PaymentServiceImpl;
import lnj.integration.TestBitcoin;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class IntegrationRunner extends Runner {
    public static PaymentService paymentService;
    private static StreamServer server;

    static {
        try {
            server = getServer();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static StreamServer getServer() throws IOException {
        LightningDaemon lightningDaemon = new LightningDaemon();
        LightningClient client = lightningDaemon.getLightningClient();
        PaymentServiceImpl paymentServiceImpl = new PaymentServiceImpl(client);
        JsonRpcServer server = new JsonRpcServer(paymentServiceImpl);

        int maxThreads = 1;
        // TODO: set the port by property
        int port = 12345;
        ServerSocket ss = new ServerSocket(port);
        StreamServer streamServer = new StreamServer(server, maxThreads, ss);
        streamServer.start();
        return streamServer;
    }

    @Override
    public Description getDescription() {
        return null;
    }

    @Override
    public void run(RunNotifier runNotifier) {
        Result res = JUnitCore.runClasses(TestBitcoin.class);
        for(Failure failure : res.getFailures()) {
            System.out.println(failure.getTrace());
        }
    }
}
