package clightning.apis;

import clightning.AbstractLightningDaemon;
import clightning.apis.optional.FundChannelParams;
import clightning.apis.optional.InvoiceParams;
import clightning.apis.optional.ListChannelsParams;
import clightning.apis.optional.PayParams;
import clightning.apis.response.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.security.provider.certpath.OCSPResponse;

import javax.annotation.CheckReturnValue;
import java.io.IOException;
import java.util.*;

public class LightningClient implements Bitcoin, Channel, Network, Payment, Utility, Plugin, Developer {

    private AbstractLightningDaemon lnd;
    private ObjectMapper mapper;

    public LightningClient(AbstractLightningDaemon lnd) {
        this.lnd = lnd;
        this.mapper = new ObjectMapper();
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
    public void autoCleanInvoice() {

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
    public void listPays() {

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
    public void payStatus() {

    }

    @Override
    public void plugin() {

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
    public void getInfo() {

    }

    @Override
    public void getLog() {

    }

    @Override
    public void help() {

    }

    @Override
    public void listConfigs() {

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

    @Override
    public void stop() {

    }
}
