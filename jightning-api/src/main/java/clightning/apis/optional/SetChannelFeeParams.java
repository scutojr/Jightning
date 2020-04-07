package clightning.apis.optional;

/**
 * Extra optional parameters for {@link clightning.apis.BasedChannel#setChannelFee(String, SetChannelFeeParams)}
 */
public class SetChannelFeeParams extends OptionalParams {
    /**
     * @param base is an optional value in millisatoshi that is added as base fee to any routed payment. If the parameter
     *             is left out, the global config value fee-base will be used again. It can be a whole number, or a whole number
     *             ending in msat or sat, or a number with three decimal places ending in sat, or a number with 1 to 11 decimal
     *             places ending in btc.
     */
    public SetChannelFeeParams setBase(long base) {
        params.put("base", base);
        return this;
    }

    /**
     * @param ppm is an optional value that is added proportionally per-millionths to any routed payment volume in satoshi.
     * For example,if ppm is 1,000and 1,000,000satoshi is being routed trhough the channel,an proportional fee of
     * 1,000satoshi is added,resulting in a0.1%fee.If the parameter is left out,the global config value will be
     * used again.
     * @return
     */
    public SetChannelFeeParams setPpm(int ppm) {
        params.put("ppm", ppm);
        return this;
    }
}
