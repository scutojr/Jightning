package lnj;

import clightning.apis.LightningClient;
import clightning.apis.response.PayResult;
import clightning.apis.response.SimpleInvoice;

public class PaymentServiceImpl implements PaymentService {
    private LightningClient client;

    public PaymentServiceImpl(LightningClient client) {
        this.client = client;
    }

    public PayResult receiveInvoice(SimpleInvoice invoice) {
        String encodedBolt11 = invoice.getBolt11();
        return client.pay(encodedBolt11);
    }
}
