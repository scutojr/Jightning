package clightning.apis.optional;

//[feerate] [minconf] [utxos]
public class WithdrawParams extends OptionalParams {
    public WithdrawParams setFeeRate(FeeRate feeRate) {
        return this;
    }

    public WithdrawParams setMinConfirmation(int minConf) {
        return this;
    }
}
