package lnj.integration;

import clightning.LightningDaemon;
import clightning.apis.LightningClient;
import clightning.apis.Output;
import clightning.apis.response.TxPrepareResult;
import lnj.PaymentService;
import lnj.runner.IntegrationRunner;
import lnj.utils.BitcoinUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

//@RunWith(IntegrationRunner.class)
public class TestBitcoin {
    private PaymentService paymentService;
    private static LightningClient client;

    static {
        try {
            client = new LightningDaemon().getLightningClient();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Before
    public void setUp() {
        paymentService = IntegrationRunner.paymentService;
    }

    @Test
    public void testAddr() {
        Assert.assertNotNull(client.feeRates());
        Assert.assertNotNull(client.newAddr());
        Assert.assertNotNull(client.newP2shSegwitAddr());
        Assert.assertNotNull(client.newBench32Addr());
    }

    @Test
    public void testTx() {
        try {
            String addr = BitcoinUtils.getNewAddress();
            Output[] outputs = new Output[]{new Output(addr, 1000 * 1000)};
            TxPrepareResult res = client.txPrepare(outputs);
            client.txDiscard(res.getTxId());

            addr = BitcoinUtils.getNewAddress();
            res = client.txPrepare(outputs);
            client.txSend(res.getTxId());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testWithdraw() {
        try {
            String addr = BitcoinUtils.getNewAddress();
            client.withDraw(addr, 1000 * 1000);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
