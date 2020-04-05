package clightning;

import clightning.apis.LightningClient;
import clightning.apis.LightningClientImpl;
import clightning.apis.response.Funds;
import com.google.common.util.concurrent.AbstractExecutionThreadService;

import java.util.List;
import java.util.Vector;

public class LightningAppKit extends AbstractExecutionThreadService {
    private boolean startLnd;
    private Network net;

    private LightningDaemon lnd;
    private LightningClient client;
    private List<LndMonitor> monitors;

    public LightningAppKit(Network net) {
        this(net, false);
    }

    public LightningAppKit(Network net, boolean startLnd) {
        this.net = net;
        this.startLnd = startLnd;
        this.monitors = new Vector<>();
    }

    @Override
    protected void startUp() throws Exception {
        lnd = new LightningDaemon(net, startLnd);
        lnd.startAsync();
        lnd.awaitRunning();

        client = new LightningClientImpl(lnd);
    }

    @Override
    protected void run() throws Exception {
        long interval = 8 * 1000;
        while (isRunning()) {
            if (!lnd.isLndRunning()) {
                for (LndMonitor monitor : monitors) {
                    monitor.onShutDown(lnd);
                }
            }
            Thread.sleep(interval);
        }
    }

    public void addMonitor(LndMonitor monitor) {
        monitors.add(monitor);
    }

    public boolean waitForConfirmed(String txId) throws InterruptedException {
        return waitForConfirmed(txId, Long.MAX_VALUE);
    }

    public boolean waitForConfirmed(String txId, long timeoutMilli) throws InterruptedException {
        long interval = 8 * 1000;
        long start = System.currentTimeMillis();
        while (true) {
            long elapsed = System.currentTimeMillis() - start;
            if (elapsed > timeoutMilli) {
                break;
            }
            boolean found = false;
            Funds funds = client.listFunds();
            for (Funds.TxOutput output : funds.getOutputs()) {
                if (output.getTxId().equals(txId)) {
                    found = true;
                    if (Funds.TxOutputStatus.confirmed == output.getStatus().get()) {
                        return true;
                    }
                    break;
                }
            }
            if (!found) {
                throw new RuntimeException("transaction " + txId + " is not found.");
            }
            Thread.sleep(interval);
        }
        return false;
    }

    public LightningClient client() {
        return client;
    }

    public LightningDaemon lightningDaemon() {
        return lnd;
    }

    public interface LndMonitor {
        void onShutDown(LightningDaemon lnd);
    }
}