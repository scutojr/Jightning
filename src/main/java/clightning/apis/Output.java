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

    public Output(String address, String display) {
        this.address = address;
        display = display;
        assert display.endsWith("msat");
        String value = display.substring(0, display.length() - "msat".length());
        amount = Long.parseLong(value) / 1000;
    }
}
