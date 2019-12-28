package clightning.apis;

import clightning.AbstractLightningDaemon;
import clightning.apis.optional.*;
import clightning.apis.response.*;
import clightning.apis.response.FeeRate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.util.*;

public class LightningClient implements Bitcoin, Channel, Network, Payment, Utility, Plugin, Developer {

    private AbstractLightningDaemon lnd;
    private ObjectMapper mapper;

    public LightningClient(AbstractLightningDaemon lnd) {
        this.lnd = lnd;
        this.mapper = new ObjectMapper().registerModule(new Jdk8Module());
    }

    private HashMap createParam() {
        return new HashMap();
    }

    private <T> List<T> toList(JsonNode array, Class<T> elementType) throws JsonProcessingException {
        ArrayList<T> list = new ArrayList<>();
        for (JsonNode node : array) {
            list.add(mapper.treeToValue(node, elementType));
        }
        return list;
    }

    @Override
    public FeeRate feeRates() throws IOException {
        Map<String, Object> params = createParam();
        params.put("style", "perkb");

        FeeRate feeRate = lnd.execute("feerates", params, FeeRate.class);
        params.put("style", "perkw");
        JsonNode feePerKw = lnd.execute("feerates", params, JsonNode.class);
        feeRate.setPerKw(mapper.treeToValue(feePerKw.get("perkw"), FeeRate.PerUnitFee.class));

        return feeRate;
    }

    private class AddrType {
        final static String BECH32 = "bech32";
        final static String P2SH_SEGWIT = "p2sh-segwit";
        final static String ALL = "all";
    }

    private JsonNode newAddrInternal(String type) throws IOException {
        Map<String, Object> params = createParam();
        params.put("addresstype", type);
        return lnd.execute("newaddr", params, JsonNode.class);
    }

    @Override
    public LightningAddress newAddr() throws IOException {
        return mapper.treeToValue(newAddrInternal(AddrType.ALL), LightningAddress.class);
    }

    @Override
    public String newBench32Addr() throws IOException {
        return newAddrInternal(AddrType.BECH32).get("address").asText();
    }

    @Override
    public String newP2shSegwitAddr() throws IOException {
        return newAddrInternal(AddrType.P2SH_SEGWIT).get("address").asText();
    }

    @Override
    public TxDiscardResult txDiscard(String txId) throws IOException {
        Map<String, Object> params = createParam();
        params.put("txid", txId);

        return lnd.execute("txdiscard", params, TxDiscardResult.class);
    }

    /**
     * TODO: add the following data to testing program
     *
     * @param outputs
     * @return
     * @throws IOException
     */
    @Override
    public TxPrepareResult txPrepare(Output[] outputs) throws IOException {
        return txPrepare(outputs, null);
    }

    @Override
    public TxPrepareResult txPrepare(Output[] outputs, TxPrepareParams optionalParams) throws IOException {
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
    public TxSendResult txSend(String txId) throws IOException {
        Map<String, Object> params = createParam();
        params.put("txid", txId);
        return lnd.execute("txsend", params, TxSendResult.class);
    }

    private WithdrawResutlt withdrawInternal(String destination, Object satoshi, WithdrawParams optionalParams) throws IOException {
        Map<String, Object> params = createParam();
        params.put("destination", destination);
        params.put("satoshi", satoshi);
        if (Objects.nonNull(optionalParams)) {
            params.putAll(optionalParams.dump());
        }
        return lnd.execute("withdraw", params, WithdrawResutlt.class);
    }

    @Override
    public WithdrawResutlt withDraw(String destination) throws IOException {
        return withdrawInternal(destination, "all", null);
    }

    @Override
    public WithdrawResutlt withDraw(String destination, WithdrawParams optionalParams) throws IOException {
        return withdrawInternal(destination, "all", optionalParams);
    }

    @Override
    public WithdrawResutlt withDraw(String destination, long satoshi) throws IOException {
        return withdrawInternal(destination, satoshi, null);
    }

    @Override
    public WithdrawResutlt withDraw(String destination, long satoshi, WithdrawParams optionalParams) throws IOException {
        return withdrawInternal(destination, satoshi, optionalParams);
    }

    @Override
    public CloseResult close(String channelId) throws IOException {
        return close(channelId, null);
    }

    @Override
    public CloseResult close(String channelId, CloseParams optionalParams) throws IOException {
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
    public String fundChannelCancel(String id) throws IOException {
        Map<String, Object> params = createParam();
        params.put("id", id);
        return lnd.execute("fundchannel_cancel", params, JsonNode.class).get("cancelled").asText();
    }

    @Override
    public FundChannelCompleteResult fundChannelComplete(String id, String txId, int txOut) throws IOException {
        Map<String, Object> params = createParam();
        params.put("id", id);
        params.put("txid", txId);
        params.put("txout", txOut);

        return lnd.execute("fundchannel_complete", params, FundChannelCompleteResult.class);
    }

    @Override
    public FundChannelStartResult fundChannelStart(String id, long amount) throws IOException {
        return fundChannelStart(id, amount, null);
    }

    @Override
    public FundChannelStartResult fundChannelStart(String id, long amount, FundChannelStartParams optionalParams) throws IOException {
        Map<String, Object> params = createParam();
        params.put("id", id);
        params.put("amount", amount);

        if (Objects.nonNull(optionalParams)) {
            params.putAll(optionalParams.dump());
        }
        return lnd.execute("fundchannel_start", params, FundChannelStartResult.class);
    }

    @Override
    public Route[] getRoute(String id, long msatoshi, double riskFactor) throws IOException {
        return getRoute(id, msatoshi, riskFactor, null);
    }

    @Override
    public Route[] getRoute(String id, long msatoshi, double riskFactor, GetRouteParams optionalParams) throws IOException {
        Map<String, Object> params = createParam();
        params.put("id", id);
        params.put("msatoshi", msatoshi);
        params.put("riskfactor", riskFactor);

        if (Objects.nonNull(optionalParams)) {
            params.putAll(optionalParams.dump());
        }

        JsonNode rsp = lnd.execute("getroute", params, JsonNode.class);
        return mapper.treeToValue(rsp.get("route"), Route[].class);
    }

    /**
     * TODO: rename response.Channel to avoid conflict with apis.Channel interface
     *
     * @return
     */
    @Override
    public List<clightning.apis.response.Channel> listChannels() throws IOException {
        return listChannels(null);
    }

    @Override
    public List<clightning.apis.response.Channel> listChannels(ListChannelsParams optionalParams) throws IOException {
        JsonNode response;
        if (Objects.isNull(optionalParams)) {
            response = lnd.execute("listchannels", JsonNode.class);
        } else {
            response = lnd.execute("listchannels", optionalParams.dump(), JsonNode.class);
        }
        JsonNode channels = response.get("channels");
        if (!channels.isArray()) {
            throw new RuntimeException(
                    "channels value of listchannels response must be an array: " + channels.toString()
            );
        }
        return toList(channels, clightning.apis.response.Channel.class);
    }

    @Override
    public void listForwards() {

    }

    @Override
    public SetChannelFeeResult setChannelFee(String channelIdOrPeerId) throws IOException {
        return setChannelFee(channelIdOrPeerId, null);
    }

    @Override
    public SetChannelFeeResult setChannelFee(String channelIdOrPeerId, SetChannelFeeParams optionalParams) throws IOException {
        Map<String, Object> params = createParam();
        params.put("id", channelIdOrPeerId);
        if (Objects.nonNull(optionalParams)) {
            params.putAll(optionalParams.dump());
        }
        return lnd.execute("setchannelfee", params, SetChannelFeeResult.class);
    }

    @Override
    public DevAddress[] devListAddrs(int bip32MaxIndex) throws IOException {
        Map<String, Object> params = createParam();
        params.put("bip32_max_index", bip32MaxIndex);
        JsonNode rsp = lnd.execute("dev-listaddrs", params, JsonNode.class);
        return mapper.treeToValue(rsp.get("addresses"), DevAddress[].class);
    }

    @Override
    public DevRescanOutput[] devRescanOutputs() throws IOException {
       JsonNode rsp = lnd.execute("dev-rescan-outputs", JsonNode.class);
       return mapper.treeToValue(rsp.get("outputs"), DevRescanOutput[].class);
    }

    @Override
    public String connect(String id, String host) throws IOException {
        return connect(id, host, null);
    }

    /**
     * TODO: ensure whether there are quotes in the return result of JsonNode.asText()
     *
     * @param id
     * @param host
     * @param port
     * @return the id of the destination lightning node
     * @throws IOException
     */
    @Override
    public String connect(String id, String host, Integer port) throws IOException {
        Map<String, Object> params = createParam();
        params.put("id", id);
        params.put("host", host);
        params.put("port", port);
        JsonNode node = lnd.execute("connect", params, JsonNode.class);
        return node.get("id").asText();
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void listNodes() {

    }

    @Override
    public void listPeers() {

    }

    @Override
    public void ping() {

    }

    @Override
    public void decodePay() {

    }

    @Override
    public void delExpiredInvoice() {

    }

    @Override
    public void delInvoice() {

    }

    @Override
    public SimpleInvoice invoice(long msatoshi, String label, String description) throws IOException {
        return invoice(msatoshi, label, description, null);
    }

    @Override
    public SimpleInvoice invoice(long msatoshi, String label, String description, InvoiceParams optionalParams) throws IOException {
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
    public List<DetailedInvoice> listInvoices() throws IOException {
        return listInvoices(null);
    }

    @Override
    public List<DetailedInvoice> listInvoices(String label) throws IOException {
        Map<String, Object> params = createParam();
        if (Objects.nonNull(label)) {
            params.put("label", label);
        }
        JsonNode node = lnd.execute("listinvoices", params, JsonNode.class);
        JsonNode invoices = node.get("invoices");
        if (!invoices.isArray()) {
            throw new RuntimeException(
                    "invoices value of listinvoices response must be an array: " + invoices.toString()
            );
        }
        return toList(invoices, DetailedInvoice.class);
    }

    @Override
    public void listSendPays() {

    }

    @Override
    public void listTransactions() {

    }

    @Override
    public void sendPay() {

    }

    @Override
    public void waitAnyInvoice() {

    }

    @Override
    public void waitInvoice() {

    }

    @Override
    public void waitSendPay() {

    }

    @Override
    public String autoCleanInvoice() throws IOException {
        return lnd.execute("autocleaninvoice", String.class);
    }

    @Override
    public String autoCleanInvoice(AutoCleanInvoiceParams optionalParams) throws IOException {
        return lnd.execute("autocleaninvoice", optionalParams.dump(), String.class);
    }

    @Override
    public FundChannel fundChannel(String id, long amountSato) throws IOException {
        return fundChannel(id, amountSato, null);
    }

    @Override
    public FundChannel fundChannel(String id, long amountSato, FundChannelParams optionalParams) throws IOException {
        Map<String, Object> params = createParam();
        params.put("id", id);
        params.put("amount", amountSato);

        if (optionalParams != null) {
            params.putAll(optionalParams.dump());
        }
        return lnd.execute("fundchannel", params, FundChannel.class);
    }

    @Override
    public PayInfo[] listPays() throws IOException {
        JsonNode node = lnd.execute("listpays", JsonNode.class);
        return mapper.treeToValue(node.get("pays"), PayInfo[].class);
    }

    /**
     * @param bolt11
     * @return Pay instance or null if not found
     */
    @Override
    public PayInfo listPays(String bolt11) throws IOException {
        Map<String, Object> params = createParam();
        params.put("bolt11", bolt11);

        JsonNode pays = lnd.execute("listpays", params, JsonNode.class).get("pays");
        return pays.size() > 0 ? mapper.treeToValue(pays.get(0), PayInfo.class) : null;
    }

    @Override
    public PayResult pay(String bolt11) throws IOException {
        return pay(bolt11, null);
    }

    @Override
    public PayResult pay(String bolt11, PayParams optionalParams) throws IOException {
        Map<String, Object> params = createParam();
        params.put("bolt11", bolt11);
        if (Objects.nonNull(optionalParams)) {
            params.putAll(optionalParams.dump());
        }
        return lnd.execute("pay", params, PayResult.class);
    }

    @Override
    public PayStatus[] payStatus() throws IOException {
        JsonNode node = lnd.execute("paystatus", JsonNode.class);
        return mapper.treeToValue(node.get("pay"), PayStatus[].class);
    }

    /**
     * @param bolt11
     * @return PayStatus instance or null if not found
     */
    @Override
    public PayStatus payStatus(String bolt11) throws IOException {
        Map<String, Object> params = createParam();
        params.put("bolt11", bolt11);
        JsonNode node = lnd.execute("paystatus", params, JsonNode.class).get("pay");
        return mapper.treeToValue(node.get(0), PayStatus.class);
    }

    @Override
    public PluginStatus plugin(PluginCommand command) throws IOException {
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
    public CheckMessageResult checkMessage(String message, String zbase) throws IOException {
        return checkMessage(message, zbase, null);
    }

    @Override
    public CheckMessageResult checkMessage(String message, String zbase, String pubKey) throws IOException {
        Map<String, Object> params = createParam();
        params.put("message", message);
        params.put("zbase", zbase);

        if (Objects.nonNull(pubKey)) {
            params.put("pubkey", pubKey);
        }
        return lnd.execute("checkmessage", params, CheckMessageResult.class);
    }

    @Override
    public LightningDaemonInfo getInfo() throws IOException {
        return lnd.execute("getinfo", LightningDaemonInfo.class);
    }

    @Override
    public LogResult getLog() throws IOException {
        return lnd.execute("getlog", LogResult.class);
    }

    @Override
    public LogResult getLog(LogLevel level) throws IOException {
        Map<String, Object> params = createParam();
        params.put("level", level.name());
        return lnd.execute("getlog", params, LogResult.class);
    }

    @Override
    public CommandUsage[] help() throws IOException {
        JsonNode helpRes = lnd.execute("help", JsonNode.class);
        JsonNode cmds = helpRes.get("help");
        Preconditions.checkState(cmds.isArray());

        return mapper.treeToValue(cmds, CommandUsage[].class);
    }

    @Override
    public CommandUsage help(String command) throws IOException {
        Preconditions.checkNotNull(command);

        Map<String, Object> params = createParam();
        params.put("command", command);

        JsonNode helpRes = lnd.execute("help", params, JsonNode.class);
        JsonNode cmds = helpRes.get("help");
        Preconditions.checkState(cmds.isArray());

        return mapper.treeToValue(cmds.get(0), CommandUsage.class);
    }

    @Override
    public Configuration listConfigs() throws IOException {
        JsonNode configs = lnd.execute("listconfigs", JsonNode.class);
        return new Configuration(configs);
    }

    @Override
    public Funds listFunds() throws IOException {
        return lnd.execute("listfunds", Funds.class);
    }

    @Override
    public SignResult signMessage(String message) throws IOException {
        Map<String, Object> params = createParam();
        params.put("message", message);
        return lnd.execute("signmessage", params, SignResult.class);
    }
}
