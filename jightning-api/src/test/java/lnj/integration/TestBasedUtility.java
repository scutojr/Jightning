package lnj.integration;

import clightning.LightningDaemon;
import clightning.Network;
import clightning.apis.LightningClient;
import clightning.apis.LightningClientImpl;
import clightning.apis.optional.LogLevel;
import clightning.apis.response.Funds;
import lnj.utils.LightningUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestBasedUtility {
    private LightningClient client;

    @Before
    public void setUp() throws IOException {
        LightningDaemon lnd = LightningUtils.getLnd(false);
        client = new LightningClientImpl(lnd);
    }

    @Test
    public void testListFunds() {
        Funds funds = client.listFunds();
    }

    @Test
    public void testAll() {
        Assert.assertNotNull(client.getInfo());
        Assert.assertNotNull(client.getLog());
        Assert.assertNotNull(client.getLog(LogLevel.info));
        Assert.assertTrue(client.help().length >= 0);
        Assert.assertNotNull(client.help("listfunds"));
        Assert.assertNotNull(client.listConfigs());
        Assert.assertNotNull(client.signMessage("message to be signed"));
    }
}
