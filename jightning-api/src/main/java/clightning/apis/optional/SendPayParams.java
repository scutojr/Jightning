package clightning.apis.optional;

public class SendPayParams extends OptionalParams {
    public SendPayParams setLabel(String label) {
        params.put("label", label);
        return this;
    }
    public SendPayParams setMsatoshi(long msatoshi) {
        params.put("msatoshi", msatoshi);
        return this;
    }

    public SendPayParams setBolt11(String bolt11) {
        params.put("bolt11", bolt11);
        return this;
    }
}
