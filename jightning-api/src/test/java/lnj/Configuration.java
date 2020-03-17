package lnj;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    public static final String CUSTOM_PLUGIN_PORT = "customPlugin.port";
    public static final String LIGHTNING_PROXY_PORT = "lightning.proxy.port";

    private Properties props;

    public Configuration() throws IOException {
        props = new Properties();
        InputStream in = Configuration.class.getClassLoader().getResourceAsStream("configuration.properties");
        props.load(in);
    }

    public String get(String prop) {
        return (String) props.get(prop);
    }

    private <T> T getInternal(String prop, Cast c, T defaultValue) {
        String value = get(prop);
        return value == null ? defaultValue : (T) c.cast(value);
    }

    public String getString(String prop, String defaultValue) {
        String value = get(prop);
        return value == null ? defaultValue : value;
    }

    public int getInt(String prop, Integer defaultValue) {
        return getInternal(prop, Integer::valueOf, defaultValue);
    }

    private interface Cast {
        Object cast(String value);
    }
}
