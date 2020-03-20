package lnj.integration;

import clightning.LightningDaemon;
import clightning.Network;
import clightning.apis.ChannelState;
import clightning.apis.LightningClient;
import clightning.apis.LightningClientImpl;
import clightning.apis.Output;
import clightning.apis.optional.ListChannelsParams;
import clightning.apis.optional.SetChannelFeeParams;
import clightning.apis.response.*;
import lnj.utils.BitcoinUtils;
import lnj.utils.LightningUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

public class TestBasedChannel {
    private LightningClient client;

    private void sleep(long milliSec) {
        try {
            Thread.sleep(milliSec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() throws IOException {
        LightningDaemon lnd = LightningUtils.getLnd(false);
        client = new LightningClientImpl(lnd);
    }

    @Test
    @Ignore
    public void testListForwards() {
        // ERROR: call the method will cause the lightning daemon exit unexpectedly
    }

    @Test
    public void testSetChannelFee() {
        SetChannelFeeParams params = new SetChannelFeeParams().setBase(2);
        for (Channel channel : client.listChannels()) {
            client.setChannelFee(channel.getShortChannelId(), params);
        }
    }

    @Test
    public void testListChannels() {
        Channel[] channels = client.listChannels();
        for (Channel channel : channels) {
            ListChannelsParams p1 = new ListChannelsParams()
                    .setShortChannelId(channel.getShortChannelId());
            Assert.assertTrue(
                    client.listChannels(p1).length > 0
            );
            ListChannelsParams p2 = new ListChannelsParams()
                    .setSource(channel.getSource());
            Assert.assertTrue(
                    client.listChannels(p2).length > 0
            );
        }
    }

    @Test
    public void testGetRoute() {
        BigDecimal decimal = new BigDecimal("18446744073709551615");
        Peer[] peers = client.listPeers();
        for (Peer peer : peers) {
            final String nodeId = peer.getId();
            Route[] routes = client.getRoute(nodeId, 1000, 0.2);
            boolean isMatched = Arrays.stream(routes).anyMatch((route) -> {
                return route.getId().equals(nodeId);
            });
            Assert.assertTrue(isMatched);
        }
    }

    @Test
    public void testChannel() throws IOException {
        int port = 9735;
        String destNodeId = null;
        String channelId = null;

        Funds funds = client.listFunds();
        for (Funds.Channel channel : funds.getChannels()) {
            if (channel.getChannelSat() > channel.getChannelTotalSat() / 2) {
                destNodeId = channel.getPeerId();
                channelId = channel.getShortChannelId().get();
                break;
            }
        }
        Assert.assertNotNull(destNodeId);
        Assert.assertNotNull(channelId);
        client.close(channelId);

        // try to create a channel and then cancel it
        long amountSatoshi = 15 * 1000 * 1000;
        String host = LightningUtils.getHost(destNodeId);
        client.connect(destNodeId, host, port);
        client.fundChannelStart(destNodeId, amountSatoshi);
        sleep(5 * 1000);
        client.fundChannelCancel(destNodeId);

        sleep(2 * 1000);
        client.connect(destNodeId, host);
        FundChannelStartResult startResult = client.fundChannelStart(destNodeId, amountSatoshi);
        TxPrepareResult txResult = client.txPrepare(new Output[]{new Output(startResult.getFundingAddress(), amountSatoshi)});
        client.fundChannelComplete(destNodeId, txResult.getTxId(), 0);
        client.txSend(txResult.getTxId());
        BitcoinUtils.generateToAddress(101, BitcoinUtils.getNewAddress());
        // generate the block and then assert the state of the funding channel
        boolean isSuccess = false;
        Funds.Channel channel = null;
        for (int i = 0; i < 60; i++) {
            for (Funds.Channel c : client.listFunds().getChannels()) {
                if (c.getPeerId().equals(destNodeId)) {
                    channel = c;
                    break;
                }
            }
            if (channel == null || channel.getState() != ChannelState.CHANNELD_NORMAL) {
                sleep(1 * 1000);
            } else {
                isSuccess = true;
                break;
            }
        }
        Assert.assertTrue(isSuccess);
    }
}
