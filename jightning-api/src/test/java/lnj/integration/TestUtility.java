package lnj.integration;

import clightning.LightningDaemon;
import clightning.apis.LightningClient;
import clightning.apis.optional.LogLevel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestUtility {
    private LightningClient client;

    @Before
    public void setUp() throws IOException {
        client = new LightningDaemon().getLightningClient();
    }

    @Test
    public void testAll() {
        Assert.assertNotNull(client.getInfo());
        Assert.assertNotNull(client.getLog());
        Assert.assertNotNull(client.getLog(LogLevel.info));
        Assert.assertTrue(client.help().length >= 0);
        Assert.assertNotNull(client.help("listfunds"));
        Assert.assertNotNull(client.listConfigs());
        Assert.assertNotNull(client.listFunds());
        Assert.assertNotNull(client.signMessage("message to be signed"));
        // TODO: add stop method, that is a new api i think
    }
}
