package lnj.integration;

import clightning.LightningDaemon;
import clightning.apis.LightningClient;
import clightning.apis.Output;
import clightning.apis.response.TxPrepareResult;
import lnj.utils.BitcoinUtils;
import lnj.utils.LightningUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestBitcoin {
    private LightningClient client;

    @Before
    public void setUp() {
        try {
            client = new LightningDaemon().getLightningClient();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
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
            Assert.assertTrue(LightningUtils.waitForFundConfirmed());
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
            BitcoinUtils.confirm();
            Assert.assertTrue(LightningUtils.waitForFundConfirmed());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
