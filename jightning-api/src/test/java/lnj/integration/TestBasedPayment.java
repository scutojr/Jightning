package lnj.integration;

import clightning.LightningDaemon;
import clightning.apis.InvoiceStatus;
import clightning.apis.LightningClient;
import clightning.apis.optional.ExpiryUnit;
import clightning.apis.optional.InvoiceParams;
import clightning.apis.response.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.util.concurrent.SettableFuture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static lnj.utils.LightningUtils.forEachPeer;

public class TestBasedPayment {
    private LightningClient client;
    private ObjectMapper mapper;

    private String generateLabel() {
        return "label-" + System.currentTimeMillis();
    }

    private String generateLabel(long ts) {
        return "label-" + ts;
    }

    @Before
    public void setUp() {
        try {
            mapper = new ObjectMapper().registerModule(new Jdk8Module());
            client = new LightningDaemon().getLightningClient();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testWaitSendPay() {
        forEachPeer((peer) -> {
            try {
                SettableFuture isTriggered = SettableFuture.create();
                SimpleInvoice invoice = peer.invoice(1000 * 1000, generateLabel(), "desc");
                Thread waitSendPay = new Thread(() -> {
                    client.waitSendPay(invoice.getPaymentHash());
                    isTriggered.set(true);
                });
                client.pay(invoice.getBolt11());
                waitSendPay.start();
                isTriggered.get(4, TimeUnit.SECONDS);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        });
    }

    @Test
    public void testWaitInvoice() {
        forEachPeer((peer) -> {
            try {
                String label = generateLabel();
                SettableFuture isTriggered = SettableFuture.create();
                Thread waitInvoice = new Thread(() -> {
                    peer.waitInvoice(label);
                    isTriggered.set(true);
                });
                SimpleInvoice invoice = peer.invoice(1000 * 1000, label, "desc");
                waitInvoice.start();
                Thread.sleep(1 * 1000);
                client.pay(invoice.getBolt11());
                isTriggered.get(100, TimeUnit.SECONDS);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        });
    }

    @Test
    @Ignore
    public void testListTransactions() {
        Assert.assertTrue(client.listTransactions().length > 0);
    }

    @Test
    public void testListSendPays() {
        forEachPeer((peer) -> {
            long msatoshi = 1000 * 1000;
            SimpleInvoice invoice = peer.invoice(msatoshi, generateLabel(), "desc");
            client.pay(invoice.getBolt11());
            Assert.assertTrue(client.listSendPays().length > 0);
        });
    }

    @Test
    public void testDelInvoice() {
        forEachPeer((peer) -> {
            String label = generateLabel();
            Assert.assertTrue(peer.listInvoices(label).length == 0);
            peer.invoice(1000 * 1000, label, "desc");
            DetailedInvoice invoice = peer.listInvoices(label)[0];
            peer.delInvoice(invoice.getLabel(), invoice.getStatus());
            Assert.assertTrue(peer.listInvoices(label).length == 0);
        });
    }

    @Test
    public void testDelExpiredInvoice() {
        forEachPeer((peer) -> {
            int cntInvoice = 5;
            long msatoshi = 1000 * 1000;
            long now = System.currentTimeMillis();
            int expirySecond = 10;

            InvoiceParams params = new InvoiceParams();
            params.setExpiry(expirySecond);
            for (int i = 0; i < cntInvoice; i++) {
                System.out.println("jladsjfklasjdlfjasdlf: " + i);
                peer.invoice(msatoshi, generateLabel(now), "desc-" + now, params);
                now += 1;
            }

            int cntUnpaid = 0;
            for (DetailedInvoice invoice : peer.listInvoices()) {
                if (invoice.getStatus() == InvoiceStatus.unpaid) {
                    cntUnpaid += 1;
                }
            }
            Assert.assertTrue(cntUnpaid >= cntInvoice);

            try {
                Thread.sleep((expirySecond + 1) * 1000);
            } catch (InterruptedException e) {
                Assert.fail(e.getMessage());
            }

            peer.delExpiredInvoice();
            for (DetailedInvoice invoice : peer.listInvoices()) {
                if (invoice.getStatus() == InvoiceStatus.unpaid) {
                    String display = "";
                    try {
                        display = mapper.writeValueAsString(invoice);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    Assert.fail("all the unpaid invoices should be deleted: " + display);
                }
            }
        });
    }

    @Test
    public void testSendPay() {
        forEachPeer((peer) -> {
            long msatoshi = 1000 * 1000;
            SimpleInvoice invoice = peer.invoice(msatoshi, generateLabel(), "desc");
            Bolt11 bolt11 = client.decodePay(invoice.getBolt11());
            Route[] routes = client.getRoute(bolt11.getPayee(), bolt11.getMsatoshi().get(), 0.1);
            SendPayResult payResult = client.sendPay(routes, bolt11.getPaymentHash());
            Assert.assertTrue(payResult.getMsatoshiSent() == msatoshi);
            Assert.assertNotNull(client.decodePay(invoice.getBolt11()));
        });
    }

    @Test
    public void testInvoice() {
        long msatoshi = 1000 * 1000;
        InvoiceParams params = new InvoiceParams();

        params.setExpiry(1, ExpiryUnit.day);
        params.setFallbacks(client.newBench32Addr(), client.newBench32Addr());
        params.setExposePrivateChannels(false);
        SimpleInvoice invoice = client.invoice(msatoshi, generateLabel(), "desc", params);
        Assert.assertNotNull(invoice);
    }
}
