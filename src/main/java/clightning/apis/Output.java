package clightning.apis;

import lombok.Getter;

@Getter
public class Output {
    private String address;
    private long amount;
    private String display;

    public Output(String address, long satoshi) {
        this.address = address;
        amount = satoshi;
    }

    public Output(String address, String amount) {
        this.address = address;
        display = amount;
    }
}
