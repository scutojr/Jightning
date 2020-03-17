package lnj.integration;

import clightning.LightningDaemon;
import clightning.apis.LightningClient;
import clightning.apis.response.Node;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestBasedNetwork {
    private LightningClient client;

    @Before
    public void setUp() throws IOException {
        LightningDaemon lnd = new LightningDaemon();
        client = lnd.getLightningClient();
    }
    @Test
    public void testListNodes() {
        Node[] nodes = client.listNodes();
        Assert.assertNotNull(nodes);
        Assert.assertTrue(nodes.length > 0);
    }
}
