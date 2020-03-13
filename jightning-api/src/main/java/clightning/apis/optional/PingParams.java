package clightning.apis.optional;

public class PingParams extends OptionalParams {
    public PingParams setLen(long len) {
        params.put("len", len);
        return this;
    }

    public PingParams setPongBytes(long pongBytes) {
        params.put("pongbytes", pongBytes);
        return this;
    }
}
