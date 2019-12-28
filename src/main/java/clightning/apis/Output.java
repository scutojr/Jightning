package clightning.apis;

import lombok.Getter;

@Getter
public class Output {
    private String address;
    private long amount;

    public Output(String address, long satoshi) {
        this.address = address;
        amount = satoshi;
    }
}
