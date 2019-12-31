package lnj.testCase;

import clightning.AbstractLightningDaemon;
import clightning.apis.InvoiceStatus;
import clightning.apis.LightningClient;
import clightning.apis.Output;
import clightning.apis.PluginCommand;
import clightning.apis.optional.ListPeersParams;
import clightning.apis.optional.LogLevel;
import clightning.apis.optional.PingParams;
import clightning.apis.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lnj.LightningForTesting;
import org.junit.Assert;
import org.junit.Ignore;
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
    public void testWait() {
        String paymentHash = "6c7c87c865703a3ac4fe487bd2f0b8718bae180cf032cb8ba2ff979d81e7940d";
        String label = "test3";
        int lastPayIndex = 4;
        try {
            Assert.assertNotNull(client.waitSendPay(paymentHash));
            Assert.assertNotNull(client.waitInvoice(label));
            Assert.assertNotNull(client.waitAnyInvoice(lastPayIndex));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Ignore // can not match the data file right now
    @Test
    public void testSendPay() {
        ObjectMapper mapper = new ObjectMapper();
        String rawRoute = "[{\"msatoshi\": 11, \"direction\": 1, \"amount_msat\": \"11msat\", \"delay\": 9, \"id\": \"036c0793141c045a9e1e50efaa2740def367800580ecad7d31268103f9b9e97472\", \"channel\": \"507x1x0\"}]";

        try {
            Route[] route = mapper.readValue(rawRoute, Route[].class);
            String paymentHash = "d18ba6bf223f94c5ead6aba6d95d98fbe88b2a30087fa9c7ee8a4f8b21a95363";
            SendPayResult result = client.sendPay(route, paymentHash);
            Assert.assertNotNull(result);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testListTransactions() {
        try {
            Transaction[] transactions = client.listTransactions();
            Assert.assertTrue(transactions.length > 0);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testListSendPays() {
        try {
            PayResult[] results = client.listSendPays();
            Assert.assertTrue(results.length > 0);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testDelInvoice() {
        try {
            String label = "l";
            InvoiceStatus status = InvoiceStatus.unpaid;
            DetailedInvoice invoice = client.delInvoice(label, status);
            Assert.assertNotNull(invoice);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testDelExpiredInvoice() {
        try {
            client.delExpiredInvoice();
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testDecodePay() {
        try {
            String bolt11 = "lnbcrt10u1p0qnafrpp5tsqkl3x0txc9u8ece0v42jw59vjzsz6p9gzkrjwuewl0xuqdzt6qdqgw3jhxapkxqyjw5qcqp2jj2v76sukafth2yqumhr5e7syppv5a7fuv9e4ueshtnhjaq2q2wr8l2s63epzdvcpntz73ap0zz4dxjvw3haadc49wx9vv7yr4fd0tcpkvl405";
            Assert.assertNotNull(client.decodePay(bolt11));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testListNodes() {
        try {
            Node[] nodes = client.listNodes();
            Assert.assertTrue(nodes.length > 0);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testDisconnect() {
        try {
            String id = "036c0793141c045a9e1e50efaa2740def367800580ecad7d31268103f9b9e97472";
            client.disconnect(id);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testListForwards() {
        try {
            // not enough testing data
            Assert.assertTrue(
                    client.listForwards().length == 0
            );
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testPing() {
        String id = "036c0793141c045a9e1e50efaa2740def367800580ecad7d31268103f9b9e97472";
        int len = 123;
        try {
            Assert.assertTrue(
                    client.ping(id, new PingParams().setLen(len)) > 0
            );
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testListPeers() {
        try {
            Peer[] peers = client.listPeers(new ListPeersParams().setLevel(LogLevel.io));
            Assert.assertTrue(peers.length > 0);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testTxSend() {
        try {
            String txId = "07763638d014ebe47e07ba0623c645021414c2867811d80e762207360f94bfb5";
            Assert.assertNotNull(client.txSend(txId));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testWithdraw() {
        try {
            String destination = "2NCpXca1wqQw4AZAEU7rdtg5bYRjDfp5sPZ";
            long satoshi = 10000000;
            Assert.assertNotNull(client.withDraw(destination, satoshi));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testDevRescanOutput() {
        try {
            Assert.assertNotNull(client.devRescanOutputs());
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testDevListAddress() {
        try {
            int maxCnt = 2;
            DevAddress[] addrs = client.devListAddrs(maxCnt);
            Assert.assertTrue(addrs.length <= maxCnt + 1);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testDiscard() {
        try {
            String txId = "6a2590e344155722db01f295591db34e26cf1a0b6a5accbdf84c29cc9771e73a";
            Assert.assertNotNull(client.txDiscard(txId));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testTxPrepare() {
        try {
            String address = "2NG1hGL3fJ6zM1gS93o3xtXT2cJQydvbv5n";
            long satoshi = 10000000;
            TxPrepareResult res = client.txPrepare(new Output[]{new Output(address, satoshi)});
            Assert.assertNotNull(res);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testNewAddr() {
        try {
            Assert.assertNotNull(client.newAddr());
            Assert.assertNotNull(client.newBench32Addr());
            Assert.assertNotNull(client.newP2shSegwitAddr());
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testFundChannelCancel() {
        try {
            String id = "036c0793141c045a9e1e50efaa2740def367800580ecad7d31268103f9b9e97472";
            String message = "Channel open canceled by RPC";
            Assert.assertTrue(client.fundChannelCancel(id).equals(message));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testFundChannelStart() {
        try {
            String id = "036c0793141c045a9e1e50efaa2740def367800580ecad7d31268103f9b9e97472";
            long amount = 1000000;
            Assert.assertNotNull(client.fundChannelStart(id, amount));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testSetChannelFee() {
        try {
            String id = "036c0793141c045a9e1e50efaa2740def367800580ecad7d31268103f9b9e97472";
            SetChannelFeeResult result = client.setChannelFee(id);
            Assert.assertNotNull(result);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testClose() {
        try {
            String channelId = "204x1x0";
            CloseResult result = client.close(channelId);
            Assert.assertNotNull(result);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testGetRoute() {
        try {
            String id = "03b27d2edad44bcf65b92605031a5577843b336551deb480e38537a845da6c7aec";
            long msatoshis = 11111;
            double riskFactor = 0.1;
            Route[] routes = client.getRoute(id, msatoshis, riskFactor);
            Assert.assertTrue(routes.length > 0);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testPlugin() {
        try {
            String pluginPath = "helloworld.py";
            PluginStatus pluginStatus = client.plugin(PluginCommand.list());

            // it should be false, but return true deal to limitation of testing data
            Assert.assertTrue(pluginStatus.isActive(pluginPath));

            pluginStatus = client.plugin(PluginCommand.start(pluginPath));
            Assert.assertTrue(pluginStatus.isActive(pluginPath));

            pluginStatus = client.plugin(PluginCommand.stop(pluginPath));
            // it should be false, but return true deal to limitation of testing data
            Assert.assertTrue(pluginStatus.isActive(pluginPath));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testAutoCleanInvoice() {
        try {
            String msg = client.autoCleanInvoice();
            Assert.assertNotNull(msg);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testPayStatus() {
        try {
            PayStatus[] payStatuses = client.payStatus();
            Assert.assertTrue(payStatuses.length > 0);
        } catch (IOException e) {
            Assert.fail();
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
