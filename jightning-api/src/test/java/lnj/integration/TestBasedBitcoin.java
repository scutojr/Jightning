package lnj.integration;

import clightning.LightningAppKit;
import clightning.Network;
import clightning.apis.LightningClient;
import clightning.apis.LightningClientImpl;
import clightning.apis.Output;
import clightning.apis.response.TxPrepareResult;
import clightning.apis.response.WithdrawResutlt;
import lnj.utils.BitcoinUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestBasedBitcoin {
    private LightningAppKit appKit;
    private LightningClient client;

    @Before
    public void setUp() {
        appKit = new LightningAppKit(Network.regtest, true);
        appKit.startAsync();
        appKit.awaitRunning();

        client = new LightningClientImpl(appKit.lightningDaemon());
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

            res = client.txPrepare(outputs);
            client.txSend(res.getTxId());
            BitcoinUtils.confirm();
            Assert.assertTrue(appKit.waitForConfirmed(res.getTxId()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testWithdraw() {
        try {
            String addr = BitcoinUtils.getNewAddress();
            WithdrawResutlt res = client.withDraw(addr, 1000 * 1000);
            BitcoinUtils.confirm();
            Assert.assertTrue(appKit.waitForConfirmed(res.getTxId()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
