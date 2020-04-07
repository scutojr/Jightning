package clightning.apis.optional;

/**
 * Extra optional parameters for {@link clightning.apis.BasedNetwork#ping(String, PingParams)}
 */
public class PingParams extends OptionalParams {
    /**
     * set the length of data sent
     *
     * @param len length of data
     * @return object being called
     */
    public PingParams setLen(long len) {
        params.put("len", len);
        return this;
    }

    /**
     * set pongBytes
     *
     * @param pongBytes length of pongBytes, default to 128
     * @return object being called
     */
    public PingParams setPongBytes(long pongBytes) {
        params.put("pongbytes", pongBytes);
        return this;
    }
}
