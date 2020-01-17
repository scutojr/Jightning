package lnj;

import clightning.apis.response.PayResult;
import clightning.apis.response.SimpleInvoice;

public interface PaymentService {
    PayResult receiveInvoice(SimpleInvoice invoice);

}
