package testCase;

import clightning.AbstractLightningDaemon;
import clightning.LightningForTesting;
import clightning.apis.LightningClient;
import clightning.apis.optional.LogLevel;
import clightning.apis.response.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static clightning.apis.response.LightningDaemonInfo.*;
import static clightning.apis.response.LightningDaemonInfo.BindingWrapper.*;

public class TestLightningDaemon {
    private AbstractLightningDaemon lnd;
    private LightningClient client;

    public TestLightningDaemon() {
        lnd = new LightningForTesting();
        client = new LightningClient(lnd);
    }

    private interface CodeWrapper {
        void apply();
    }

    private void assertThrows(CodeWrapper code) {
        try {
            code.apply();
            Assert.fail("it's supposed to throw an exception!");
        } catch (Exception exp) {
        }
    }

    @Test
    public void testListPays() {
        String bolt11 = "lnbcrt10u1p0qrdp4pp5270l2ujwnhrdkuw7cq5ttjs3lglm5mnh94k944d5m67h480zyuuqdqyvscsxqyjw5qcqp2xxpkyureu72g7suzwudppj9efwap0cs3k76ngm5ga9d9uzku57tn2dgx45vdgrxmj9nz7082krl7lnxc4c2p9avtzm9gk6j83nsw27sqyu80mu";
        try {
            PayInfo[] payInfos = client.listPays();
            Assert.assertTrue(payInfos.length > 0);

            PayInfo payInfo = client.listPays(bolt11);
            Assert.assertNotNull(payInfo);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testListConfigs() {
        try {
            Configuration conf = client.listConfigs();
            Assert.assertTrue(conf.propNames().hasNext());
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testHelp() {
        try {
            CommandUsage[] commandUsages = client.help();
            Assert.assertTrue(commandUsages.length > 0);

            CommandUsage cmd = client.help("listconfigs");
            Assert.assertNotNull(cmd);
            // TODO: test input with a non-existed command
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testGetLog() {
        try {
            LogResult logResult = client.getLog();
            LogResult logResultIo = client.getLog(LogLevel.io);
            Assert.assertNotNull(logResult);
            Assert.assertNotNull(logResultIo);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testGetInfo() {
        try {
            LightningDaemonInfo res = client.getInfo();
            Assert.assertNotNull(res);
            BindingWrapper[] bindings = res.getBinding();
            for (BindingWrapper binding : bindings) {
                switch (Type.getType(binding.getType())) {
                    case LOCAL_SOCKET:
                        BindingWrapper.LocalSocket localSocket = binding.getLocalSocket();
                        assertThrows(() -> binding.getAnyProtocol());
                        break;
                    case ANY_PROTOCOL:
                        BindingWrapper.AnyProtocol anyProtocol = binding.getAnyProtocol();
                        assertThrows(() -> binding.getLocalSocket());
                        break;
                    case TOR_GENERATED_ADDRESS:
                        BindingWrapper.TorGeneratedAddress torGeneratedAddress = binding.getTorGeneratedAddress();
                        assertThrows(() -> binding.getLocalSocket());
                        break;
                    case UNRESOLVED:
                        BindingWrapper.Unresolved unresolved = binding.getUnresolved();
                        assertThrows(() -> binding.getLocalSocket());
                        break;
                    case IPV4:
                        BindingWrapper.IpOrTor ipv4 = binding.getIpv4();
                        assertThrows(() -> binding.getLocalSocket());
                        break;
                    case IPV6:
                        BindingWrapper.IpOrTor ipv6 = binding.getIpv6();
                        assertThrows(() -> binding.getLocalSocket());
                        break;
                    case TORV2:
                        BindingWrapper.IpOrTor torv2 = binding.getTorv2();
                        assertThrows(() -> binding.getLocalSocket());
                        break;
                    case TORV3:
                        BindingWrapper.IpOrTor torv3 = binding.getTorv3();
                        assertThrows(() -> binding.getLocalSocket());
                        break;
                    default:
                        Assert.fail();
                }
            }
        } catch (IOException e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void testCheckMessage() {
        String msg = "xxyy";
        String zbase = "dhybt3bt8dq8wgpk9344puz6bqu7d5bwrakcoes8bbpefmujthokrwn8yroczabc3ij7fsh3gmqzywbqsdgw39ghoxy3jj3zn7kw1apm";
        try {
            CheckMessageResult res = client.checkMessage(msg, zbase);
            Assert.assertNotNull(res);
        } catch (IOException e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void testSignMessage() {
        String msg = "xxyy";
        try {
            SignResult res = client.signMessage(msg);
            Assert.assertNotNull(res);
        } catch (IOException e) {
            Assert.fail(e.toString());
        }
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
            Assert.fail();
        }
    }

    @Test
    public void testLightningDaemon() {
        try {
            LightningAddress addr = client.newAddr();
            Assert.assertNotNull(addr);
        } catch (IOException e) {
            Assert.fail();
        }
    }
}
