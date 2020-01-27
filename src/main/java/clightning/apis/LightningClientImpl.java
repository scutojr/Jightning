package clightning.apis;

import clightning.AbstractLightningDaemon;
import clightning.apis.optional.*;
import clightning.apis.response.*;
import clightning.apis.response.FeeRate;
import clightning.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LightningClientImpl implements LightningClient {

    private AbstractLightningDaemon lnd;
    private ObjectMapper mapper;

    public LightningClientImpl(AbstractLightningDaemon lnd) {
        this.lnd = lnd;
        this.mapper = new ObjectMapper().registerModule(new Jdk8Module());
    }

    private HashMap createParam() {
        return new HashMap();
    }

    @Override
    public FeeRate feeRates() {
        Map<String, Object> params = createParam();
        params.put("style", "perkb");

        FeeRate feeRate = lnd.execute("feerates", params, FeeRate.class);
        params.put("style", "perkw");
        JsonNode feePerKw = lnd.execute("feerates", params, JsonNode.class);
        feeRate.setPerKw(JsonUtil.convert(feePerKw.get("perkw"), FeeRate.PerUnitFee.class));

        return feeRate;
    }

    private class AddrType {
        final static String BECH32 = "bech32";
        final static String P2SH_SEGWIT = "p2sh-segwit";
        final static String ALL = "all";
    }

    private JsonNode newAddrInternal(String type) {
        Map<String, Object> params = createParam();
        params.put("addresstype", type);
        return lnd.execute("newaddr", params, JsonNode.class);
    }

    @Override
    public LightningAddress newAddr() {
        return JsonUtil.convert(newAddrInternal(AddrType.ALL), LightningAddress.class);
    }

    @Override
    public String newBench32Addr() {
        return newAddrInternal(AddrType.BECH32).get("address").asText();
    }

    @Override
    public String newP2shSegwitAddr() {
        return newAddrInternal(AddrType.P2SH_SEGWIT).get("address").asText();
    }

    @Override
    public TxDiscardResult txDiscard(String txId) {
        Map<String, Object> params = createParam();
        params.put("txid", txId);

        return lnd.execute("txdiscard", params, TxDiscardResult.class);
    }

    /**
     * TODO: add the following data to testing program
     *
     * @param outputs
     * @return
     * @
     */
    @Override
    public TxPrepareResult txPrepare(Output[] outputs) {
        return txPrepare(outputs, null);
    }

    @Override
    public TxPrepareResult txPrepare(Output[] outputs, TxPrepareParams optionalParams) {
        Map<String, Object> params = createParam();

        ArrayNode os = mapper.createArrayNode();
        for (Output output : outputs) {
            ObjectNode obj = mapper.createObjectNode();
            obj.put(output.getAddress(), output.getAmount());
            os.add(obj);
        }

        params.put("outputs", os);
        if (Objects.nonNull(optionalParams)) {
            params.putAll(optionalParams.dump());
        }

        return lnd.execute("txprepare", params, TxPrepareResult.class);
    }

    @Override
    public TxSendResult txSend(String txId) {
        Map<String, Object> params = createParam();
        params.put("txid", txId);
        return lnd.execute("txsend", params, TxSendResult.class);
    }

    private WithdrawResutlt withdrawInternal(String destination, Object satoshi, WithdrawParams optionalParams) {
        Map<String, Object> params = createParam();
        params.put("destination", destination);
        params.put("satoshi", satoshi);
        if (Objects.nonNull(optionalParams)) {
            params.putAll(optionalParams.dump());
        }
        return lnd.execute("withdraw", params, WithdrawResutlt.class);
    }

    @Override
    public WithdrawResutlt withDraw(String destination) {
        return withdrawInternal(destination, "all", null);
    }

    @Override
    public WithdrawResutlt withDraw(String destination, WithdrawParams optionalParams) {
        return withdrawInternal(destination, "all", optionalParams);
    }

    @Override
    public WithdrawResutlt withDraw(String destination, long satoshi) {
        return withdrawInternal(destination, satoshi, null);
    }

    @Override
    public WithdrawResutlt withDraw(String destination, long satoshi, WithdrawParams optionalParams) {
        return withdrawInternal(destination, satoshi, optionalParams);
    }

    @Override
    public CloseResult close(String channelOrNodeId) {
        return close(channelOrNodeId, null);
    }

    @Override
    public CloseResult close(String channelId, CloseParams optionalParams) {
        Map<String, Object> params = createParam();
        params.put("id", channelId);
        if (Objects.nonNull(optionalParams)) {
            params.putAll(optionalParams.dump());
        }
        return lnd.execute("close", params, CloseResult.class);
    }

    /**
     * @param id the node id
     * @return
     */
    @Override
    public String fundChannelCancel(String id) {
        Map<String, Object> params = createParam();
        params.put("id", id);
        return lnd.execute("fundchannel_cancel", params, JsonNode.class).get("cancelled").asText();
    }

    @Override
    public FundChannelCompleteResult fundChannelComplete(String id, String txId, int txOut) {
        Map<String, Object> params = createParam();
        params.put("id", id);
        params.put("txid", txId);
        params.put("txout", txOut);

        return lnd.execute("fundchannel_complete", params, FundChannelCompleteResult.class);
    }

    @Override
    public FundChannelStartResult fundChannelStart(String id, long amount) {
        return fundChannelStart(id, amount, null);
    }

    @Override
    public FundChannelStartResult fundChannelStart(String id, long amount, FundChannelStartParams optionalParams) {
        Map<String, Object> params = createParam();
        params.put("id", id);
        params.put("amount", amount);

        if (Objects.nonNull(optionalParams)) {
            params.putAll(optionalParams.dump());
        }
        return lnd.execute("fundchannel_start", params, FundChannelStartResult.class);
    }

    @Override
    public Route[] getRoute(String id, long msatoshi, double riskFactor) {
        return getRoute(id, msatoshi, riskFactor, null);
    }

    @Override
    public Route[] getRoute(String id, long msatoshi, double riskFactor, GetRouteParams optionalParams) {
        Map<String, Object> params = createParam();
        params.put("id", id);
        params.put("msatoshi", msatoshi);
        params.put("riskfactor", riskFactor);

        if (Objects.nonNull(optionalParams)) {
            params.putAll(optionalParams.dump());
        }

        JsonNode rsp = lnd.execute("getroute", params, JsonNode.class);
        return JsonUtil.convert(rsp.get("route"), Route[].class);
    }

    /**
     * TODO: rename response.Channel to avoid conflict with apis.Channel interface
     *
     * @return
     */
    @Override
    public clightning.apis.response.Channel[] listChannels() {
        return listChannels(null);
    }

    @Override
    public clightning.apis.response.Channel[] listChannels(ListChannelsParams optionalParams) {
        JsonNode response;
        if (Objects.isNull(optionalParams)) {
            response = lnd.execute("listchannels", JsonNode.class);
        } else {
            response = lnd.execute("listchannels", optionalParams.dump(), JsonNode.class);
        }
        JsonNode channels = response.get("channels");
        return JsonUtil.convert(channels, clightning.apis.response.Channel[].class);
    }

    @Override
    public Forward[] listForwards() {
        JsonNode rsp = lnd.execute("listforwards", JsonNode.class);
        return JsonUtil.convert(rsp.get("forwards"), Forward[].class);
    }

    @Override
    public SetChannelFeeResult setChannelFee(String channelIdOrPeerId) {
        return setChannelFee(channelIdOrPeerId, null);
    }

    @Override
    public SetChannelFeeResult setChannelFee(String channelIdOrPeerId, SetChannelFeeParams optionalParams) {
        Map<String, Object> params = createParam();
        params.put("id", channelIdOrPeerId);
        if (Objects.nonNull(optionalParams)) {
            params.putAll(optionalParams.dump());
        }
        return lnd.execute("setchannelfee", params, SetChannelFeeResult.class);
    }

    @Override
    public DevAddress[] devListAddrs(int bip32MaxIndex) {
        Map<String, Object> params = createParam();
        params.put("bip32_max_index", bip32MaxIndex);
        JsonNode rsp = lnd.execute("dev-listaddrs", params, JsonNode.class);
        return JsonUtil.convert(rsp.get("addresses"), DevAddress[].class);
    }

    @Override
    public DevRescanOutput[] devRescanOutputs() {
        JsonNode rsp = lnd.execute("dev-rescan-outputs", JsonNode.class);
        return JsonUtil.convert(rsp.get("outputs"), DevRescanOutput[].class);
    }

    @Override
    public String connect(String id, String host) {
        return connect(id, host, null);
    }

    /**
     * TODO: ensure whether there are quotes in the return result of JsonNode.asText()
     *
     * @param id
     * @param host
     * @param port
     * @return the id of the destination lightning node
     * @
     */
    @Override
    public String connect(String id, String host, Integer port) {
        Map<String, Object> params = createParam();
        params.put("id", id);
        params.put("host", host);
        if (Objects.nonNull(port)) {
            params.put("port", port);
        }
        JsonNode node = lnd.execute("connect", params, JsonNode.class);
        return node.get("id").asText();
    }

    @Override
    public void disconnect(String id) {
        disconnect(id, false);
    }

    @Override
    public void disconnect(String id, boolean force) {
        Map<String, Object> params = createParam();
        params.put("id", id);
        params.put("force", force);
        lnd.execute("disconnect", params, JsonNode.class);
    }

    @Override
    public Node[] listNodes() {
        return listNodes(null);
    }

    @Override
    public Node[] listNodes(String id) {
        JsonNode rsp;
        if (Objects.nonNull(id)) {
            Map<String, Object> params = createParam();
            params.put("id", id);
            rsp = lnd.execute("listnodes", params, JsonNode.class);
        } else {
            rsp = lnd.execute("listnodes", JsonNode.class);
        }
        return JsonUtil.convert(rsp.get("nodes"), Node[].class);
    }

    @Override
    public Peer[] listPeers() {
        return listPeers(null);
    }

    @Override
    public Peer[] listPeers(ListPeersParams optionalParams) {
        JsonNode rsp;
        String method = "listpeers";
        if (Objects.nonNull(optionalParams)) {
            rsp = lnd.execute(method, optionalParams.dump(), JsonNode.class);
        } else {
            rsp = lnd.execute(method, JsonNode.class);
        }
        return JsonUtil.convert(rsp.get("peers"), Peer[].class);
    }

    @Override
    public int ping(String id) {
        return ping(id, null);
    }

    @Override
    public int ping(String id, PingParams optionalParams) {
        Map<String, Object> params = createParam();
        params.put("id", id);

        if (Objects.nonNull(optionalParams)) {
            params.putAll(optionalParams.dump());
        }
        return lnd.execute("ping", params, JsonNode.class).get("totlen").asInt();
    }

    @Override
    public Bolt11 decodePay(String bolt11) {
        return decodePay(bolt11, null);
    }

    @Override
    public Bolt11 decodePay(String bolt11, String description) {
        Map<String, Object> params = createParam();
        params.put("bolt11", bolt11);
        if (Objects.nonNull(description)) {
            params.put("description", description);
        }
        return lnd.execute("decodepay", params, Bolt11.class);
    }

    @Override
    public void delExpiredInvoice() {
        lnd.execute("delexpiredinvoice", JsonNode.class);
    }

    @Override
    public void delExpiredInvoice(int maxExpiryTimeSec) {
        Map<String, Object> params = createParam();
        params.put("maxexpirytime", maxExpiryTimeSec);
        lnd.execute("delexpiredinvoice", params, JsonNode.class);
    }

    @Override
    public DetailedInvoice delInvoice(String label, InvoiceStatus status) {
        Map<String, Object> params = createParam();
        params.put("label", label);
        params.put("status", status);
        return lnd.execute("delinvoice", params, DetailedInvoice.class);
    }

    @Override
    public SimpleInvoice invoice(long msatoshi, String label, String description) {
        return invoice(msatoshi, label, description, null);
    }

    @Override
    public SimpleInvoice invoice(long msatoshi, String label, String description, InvoiceParams optionalParams) {
        Map<String, Object> params = createParam();
        params.put("msatoshi", msatoshi);
        params.put("label", label);
        params.put("description", description);
        if (Objects.nonNull(optionalParams)) {
            params.putAll(optionalParams.dump());
        }
        return lnd.execute("invoice", params, SimpleInvoice.class);
    }

    @Override
    public DetailedInvoice[] listInvoices() {
        return listInvoices(null);
    }

    @Override
    public DetailedInvoice[] listInvoices(String label) {
        Map<String, Object> params = createParam();
        if (Objects.nonNull(label)) {
            params.put("label", label);
        }
        JsonNode node = lnd.execute("listinvoices", params, JsonNode.class);
        JsonNode invoices = node.get("invoices");

        return JsonUtil.convert(invoices, DetailedInvoice[].class);
    }

    @Override
    public PayResult[] listSendPays() {
        return listSendPays(null);
    }

    @Override
    public PayResult[] listSendPays(ListSendPaysParams optionalParams) {
        JsonNode node;
        if (Objects.nonNull(optionalParams)) {
            node = lnd.execute("listsendpays", optionalParams.dump(), JsonNode.class);
        } else {
            node = lnd.execute("listsendpays", JsonNode.class);
        }
        return JsonUtil.convert(node.get("payments"), PayResult[].class);
    }


    @Override
    public Transaction[] listTransactions() {
        JsonNode rsp = lnd.execute("listtransactions", JsonNode.class);
        return JsonUtil.convert(rsp.get("transactions"), Transaction[].class);
    }

    @Override
    public SendPayResult sendPay(Route[] route, String paymentHash) {
        return sendPay(route, paymentHash, null);
    }

    @Override
    public SendPayResult sendPay(Route[] route, String paymentHash, SendPayParams optionalParams) {
        Map<String, Object> params = createParam();
        params.put("route", route);
        params.put("payment_hash", paymentHash);

        if (Objects.nonNull(optionalParams)) {
            params.putAll(optionalParams.dump());
        }
        return lnd.execute("sendpay", params, SendPayResult.class);
    }

    @Override
    public DetailedInvoice waitAnyInvoice(int lastPayIndex) {
        Map<String, Object> params = createParam();
        params.put("lastpay_index", lastPayIndex);

        return lnd.execute("waitanyinvoice", params, DetailedInvoice.class);
    }

    @Override
    public DetailedInvoice waitInvoice(String label) {
        Map<String, Object> params = createParam();
        params.put("label", label);
        return lnd.execute("waitinvoice", params, DetailedInvoice.class);
    }

    @Override
    public PayResult waitSendPay(String paymentHash) {
        Map<String, Object> params = createParam();
        params.put("payment_hash", paymentHash);
        return lnd.execute("waitsendpay", params, PayResult.class);
    }

    @Override
    public PayResult waitSendPay(String paymentHash, long timeout) {
        Map<String, Object> params = createParam();
        params.put("payment_hash", paymentHash);
        params.put("timeout", timeout);
        return lnd.execute("waitsendpay", params, PayResult.class);
    }

    @Override
    public String autoCleanInvoice() {
        return lnd.execute("autocleaninvoice", String.class);
    }

    @Override
    public String autoCleanInvoice(AutoCleanInvoiceParams optionalParams) {
        return lnd.execute("autocleaninvoice", optionalParams.dump(), String.class);
    }

    @Override
    public FundChannel fundChannel(String id, long amountSato) {
        return fundChannel(id, amountSato, null);
    }

    @Override
    public FundChannel fundChannel(String id, long amountSato, FundChannelParams optionalParams) {
        Map<String, Object> params = createParam();
        params.put("id", id);
        params.put("amount", amountSato);

        if (optionalParams != null) {
            params.putAll(optionalParams.dump());
        }
        return lnd.execute("fundchannel", params, FundChannel.class);
    }

    @Override
    public PayInfo[] listPays() {
        JsonNode node = lnd.execute("listpays", JsonNode.class);
        return JsonUtil.convert(node.get("pays"), PayInfo[].class);
    }

    /**
     * @param bolt11
     * @return Pay instance or null if not found
     */
    @Override
    public PayInfo listPays(String bolt11) {
        Map<String, Object> params = createParam();
        params.put("bolt11", bolt11);

        JsonNode pays = lnd.execute("listpays", params, JsonNode.class).get("pays");
        return pays.size() > 0 ? JsonUtil.convert(pays.get(0), PayInfo.class) : null;
    }

    @Override
    public PayResult pay(String bolt11) {
        return pay(bolt11, null);
    }

    @Override
    public PayResult pay(String bolt11, PayParams optionalParams) {
        Map<String, Object> params = createParam();
        params.put("bolt11", bolt11);
        if (Objects.nonNull(optionalParams)) {
            params.putAll(optionalParams.dump());
        }
        return lnd.execute("pay", params, PayResult.class);
    }

    @Override
    public PayStatus[] payStatus() {
        JsonNode node = lnd.execute("paystatus", JsonNode.class);
        return JsonUtil.convert(node.get("pay"), PayStatus[].class);
    }

    /**
     * @param bolt11
     * @return PayStatus instance or null if not found
     */
    @Override
    public PayStatus payStatus(String bolt11) {
        Map<String, Object> params = createParam();
        params.put("bolt11", bolt11);
        JsonNode node = lnd.execute("paystatus", params, JsonNode.class).get("pay");
        return JsonUtil.convert(node.get(0), PayStatus.class);
    }

    @Override
    public PluginStatus plugin(PluginCommand command) {
        if (command.getCommand().equals("stop")) {
            lnd.execute("plugin", command.getParams(), JsonNode.class);
            command = PluginCommand.list();
        }

        JsonNode rsp = lnd.execute("plugin", command.getParams(), JsonNode.class);
        JsonNode plugins = rsp.get("plugins");

        PluginStatus pluginStatus = new PluginStatus();
        for (JsonNode plugin : plugins) {
            String name = plugin.get("name").asText();
            boolean active = plugin.get("active").asBoolean();

            pluginStatus.updatePlugin(name, active);
        }
        return pluginStatus;
    }

    @Override
    public void check() {

    }

    @Override
    public CheckMessageResult checkMessage(String message, String zbase) {
        return checkMessage(message, zbase, null);
    }

    @Override
    public CheckMessageResult checkMessage(String message, String zbase, String pubKey) {
        Map<String, Object> params = createParam();
        params.put("message", message);
        params.put("zbase", zbase);

        if (Objects.nonNull(pubKey)) {
            params.put("pubkey", pubKey);
        }
        return lnd.execute("checkmessage", params, CheckMessageResult.class);
    }

    @Override
    public LightningDaemonInfo getInfo() {
        return lnd.execute("getinfo", LightningDaemonInfo.class);
    }

    @Override
    public LogResult getLog() {
        return lnd.execute("getlog", LogResult.class);
    }

    @Override
    public LogResult getLog(LogLevel level) {
        Map<String, Object> params = createParam();
        params.put("level", level.name());
        return lnd.execute("getlog", params, LogResult.class);
    }

    @Override
    public CommandUsage[] help() {
        JsonNode helpRes = lnd.execute("help", JsonNode.class);
        JsonNode cmds = helpRes.get("help");
        Preconditions.checkState(cmds.isArray());

        return JsonUtil.convert(cmds, CommandUsage[].class);
    }

    @Override
    public CommandUsage help(String command) {
        Preconditions.checkNotNull(command);

        Map<String, Object> params = createParam();
        params.put("command", command);

        JsonNode helpRes = lnd.execute("help", params, JsonNode.class);
        JsonNode cmds = helpRes.get("help");
        Preconditions.checkState(cmds.isArray());

        return JsonUtil.convert(cmds.get(0), CommandUsage.class);
    }

    @Override
    public Configuration listConfigs() {
        JsonNode configs = lnd.execute("listconfigs", JsonNode.class);
        return new Configuration(configs);
    }

    @Override
    public Funds listFunds() {
        return lnd.execute("listfunds", Funds.class);
    }

    @Override
    public SignResult signMessage(String message) {
        Map<String, Object> params = createParam();
        params.put("message", message);
        return lnd.execute("signmessage", params, SignResult.class);
    }
}
