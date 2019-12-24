package testCase;

import clightning.AbstractLightningDaemon;
import clightning.LightningForTesting;
import clightning.apis.LightningClient;
import clightning.apis.response.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestLightningDaemon {
    private AbstractLightningDaemon lnd;
    private LightningClient client;

    public TestLightningDaemon() {
        lnd = new LightningForTesting();
        client = new LightningClient(lnd);
    }

    @Test
    public void testPay() {
        String bolt11 = "lnbcrt10u1p0qrdp4pp5270l2ujwnhrdkuw7cq5ttjs3lglm5mnh94k944d5m67h480zyuuqdqyvscsxqyjw5qcqp2xxpkyureu72g7suzwudppj9efwap0cs3k76ngm5ga9d9uzku57tn2dgx45vdgrxmj9nz7082krl7lnxc4c2p9avtzm9gk6j83nsw27sqyu80mu";
        try {
            PayResult payResult = client.pay(bolt11);
            Assert.assertNotNull(payResult);
        } catch (IOException e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void testListInvoices() {
        String label = "l";
        try {
            List<DetailedInvoice> invoices = client.listInvoices(label);
            Assert.assertTrue(invoices.size() > 0);
        } catch (IOException e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void testInvoice() {
        try {
            SimpleInvoice invoice = client.invoice(123456789, "l", "d");
            Assert.assertNotNull(invoice);
        } catch (IOException e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void testListChannels() {
        try {
            List<Channel> channels = client.listChannels();
            Assert.assertTrue(channels.size() > 0);
        } catch (IOException e) {
            Assert.fail(e.toString());
        }
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
