package lnj.integration;

import clightning.LightningDaemon;
import clightning.apis.ChannelState;
import clightning.apis.LightningClient;
import clightning.apis.optional.AutoCleanInvoiceParams;
import clightning.apis.optional.InvoiceParams;
import clightning.apis.response.Funds;
import clightning.apis.response.SimpleInvoice;
import lnj.utils.BitcoinUtils;
import lnj.utils.LightningUtils;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static lnj.utils.LightningUtils.forEachPeer;

public class TestPlugin {
    private LightningClient client;

    @Before
    public void setUp() throws IOException {
        client = new LightningDaemon().getLightningClient();
    }

    private String createLabel() {
        return "label-" + System.currentTimeMillis();
    }

    @Test
    public void testAutoCleanInvoice() throws InterruptedException {
        int cntInvoice = client.listInvoices().length;
        int cntExpired = 5;
        int invoiceExpiredSec = 5;
        int expiredBy = 2;
        for (int i = 0; i < cntExpired; i++) {
            long msatoshi = 1000 * 1000;
            client.invoice(msatoshi, createLabel(), "desc", new InvoiceParams().setExpiry(invoiceExpiredSec));
        }
        Assert.assertTrue(client.listInvoices().length >= cntInvoice + cntExpired);

        client.autoCleanInvoice(new AutoCleanInvoiceParams().setCycleSeconds(1).setExpiredBy(expiredBy));
        Thread.sleep((invoiceExpiredSec + expiredBy + 5) * 1000);
        Assert.assertTrue(client.listInvoices().length <= cntInvoice);
    }

    @Test
    public void testPay() {
        forEachPeer((peer) -> {
            long msatoshi = 1000 * 1000;
            SimpleInvoice invoice = peer.invoice(msatoshi, createLabel(), "desc");
            int cntBefore = client.listPays().length;
            client.pay(invoice.getBolt11());
            Assert.assertTrue(client.listPays().length > cntBefore);
            Assert.assertTrue(client.payStatus().length > 0);
            Assert.assertNotNull(client.payStatus(invoice.getBolt11()));
        });
    }

    @SneakyThrows
    @Test
    public void testFundChannel() {
        Funds funds = client.listFunds();
        Funds.Channel channel = funds.getChannels()[0];
        String peerId = channel.getPeerId();
        client.close(peerId);

        client.connect(peerId, LightningUtils.getHost(peerId));
        client.fundChannel(peerId, channel.getChannelTotalSat());
        BitcoinUtils.generateToAddress(101, BitcoinUtils.getNewAddress());
        Boolean isSucceed = false;
        for (int i = 0; i < 32; i++) {
            isSucceed = Arrays.stream(client.listFunds().getChannels()).anyMatch(
                    (c) -> c.getPeerId().equals(peerId) && c.getState() == ChannelState.CHANNELD_NORMAL
            );
            if (isSucceed) {
                break;
            } else {
                Thread.sleep(1 * 1000);
            }
        }
        Assert.assertTrue(isSucceed);
    }
}
