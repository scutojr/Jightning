package testCase;

import clightning.AbstractLightningDaemon;
import clightning.LightningForTesting;
import clightning.apis.LightningClient;
import clightning.apis.response.FeeRate;
import clightning.apis.response.FundChannel;
import clightning.apis.response.Funds;
import clightning.apis.response.LightningAddress;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TestLightningDaemon {
    private AbstractLightningDaemon lnd;
    private LightningClient client;

    public TestLightningDaemon() {
        lnd = new LightningForTesting();
        client = new LightningClient(lnd);
    }

    @Test
    public void testFundChannel() {
        String id = "0265197a63b374393904a418940500fb840c62f0a0a672ecb69561b491ab241dec";
        int amount = 10000000;
        try {
            FundChannel response = client.fundChannel(id, amount);
            Assert.assertNotNull(response);
        } catch (IOException e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void testListFunds() {
        try {
            Funds funds = client.listFunds();
            Assert.assertNotNull(funds);
            Assert.assertTrue(funds.getChannels().length > 0);
            Assert.assertTrue(funds.getOutputs().length > 0);
        } catch (IOException e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void testConnect() {
        String id = "0265197a63b374393904a418940500fb840c62f0a0a672ecb69561b491ab241dec";
        String host = "alice";
        try {
            String returnedId = client.connect(id, host);
            Assert.assertTrue(returnedId.equals(id));
        } catch (IOException e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void testGetFeeRates() {
        try {
            FeeRate feeRate = client.feeRates();
            Assert.assertNotNull(feeRate);
            Assert.assertNotNull(feeRate.getPerKb());
            Assert.assertNotNull(feeRate.getPerKw());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLightningDaemon() {
        try {
            LightningAddress addr = client.newAddr();
            Assert.assertNotNull(addr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
