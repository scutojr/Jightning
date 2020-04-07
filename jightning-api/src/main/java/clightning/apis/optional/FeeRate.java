package clightning.apis.optional;

import lombok.Data;

/**
 * Fee rate
 */
@Data
public class FeeRate {
    private String value;

    private FeeRate(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static FeeRate urgent() {
        return new FeeRate("urgent");
    }

    public static FeeRate normal() {
        return new FeeRate("normal");
    }

    public static FeeRate slow() {
        return new FeeRate("slow");
    }

    public static FeeRate perKw(long satoshi) {
        return new FeeRate(satoshi + "perkw");
    }

    public static FeeRate perKb(long satoshi) {
        return new FeeRate(satoshi + "perkb");
    }
}
