package testCase;

import clightning.AbstractLightningDaemon;
import clightning.LightningForTesting;
import clightning.apis.LightningClient;
import clightning.apis.response.FeeRate;
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
