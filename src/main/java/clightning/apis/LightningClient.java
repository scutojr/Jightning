package clightning.apis;

import clightning.AbstractLightningDaemon;
import clightning.apis.option.FundChannelParams;
import clightning.apis.response.FeeRate;
import clightning.apis.response.FundChannel;
import clightning.apis.response.Funds;
import clightning.apis.response.LightningAddress;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

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

    @Override
    public FeeRate feeRates() throws IOException {
        HashMap params = createParam();
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

    @Override
    public void listChannels() {

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
     * @param id
     * @param host
     * @param port
     * @return the id of the destination lightning node
     * @throws IOException
     */
    @Override
    public String connect(String id, String host, Integer port) throws IOException {
        HashMap params = createParam();
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
    public void invoice() {

    }

    @Override
    public void listInvoices() {

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
        HashMap params = createParam();
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
    public void pay() {

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
    public void checkMessage() {

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
    public void signMessage() {

    }

    @Override
    public void stop() {

    }
}
