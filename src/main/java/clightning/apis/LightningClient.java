package clightning.apis;

import clightning.AbstractLightningDaemon;
import clightning.apis.optional.*;
import clightning.apis.response.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Override
    public LightningAddress newAddr() throws IOException {
        HashMap<String, String> params = new HashMap();
        params.put("addresstype", "all");
        return lnd.execute("newaddr", params, LightningAddress.class);
    }

    @Override
    public String newBench32Addr() {
        return null;
    }

    @Override
    public String newP2shSegwitAddr() {
        return null;
    }

    @Override
    public void txDiscard() {

    }

    @Override
    public void txPrepare() {

    }

    @Override
    public void txSend() {

    }

    @Override
    public void close() {

    }

    @Override
    public void fundChannelCancel() {

    }

    @Override
    public void fundChannelComplete() {

    }

    @Override
    public void fundChannelStart() {

    }

    @Override
    public void getRoute() {

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
    public void setChannelFee() {

    }

    @Override
    public void devListAddrs() {

    }

    @Override
    public void devRescanOutputs() {

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
     *
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
     *
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
