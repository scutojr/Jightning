package clightning.apis.optional;

/**
 * expiry: <numeric value>[s|m|h|d|w], indicate seconds, minutes, hours, days
 * and weeks respectively, s by
 */
public enum ExpiryUnit {
    second("s"),
    minute("m"),
    hour("h"),
    day("d"),
    week("w");

    private String sign;

    ExpiryUnit(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }
}
