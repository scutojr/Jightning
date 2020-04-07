package clightning.apis.response;

import clightning.apis.BasedUtility;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;

/**
 * Response of {@link BasedUtility#listConfigs()}
 */
public class Configuration {
    private JsonNode props;

    public Configuration(JsonNode props) {
        this.props = props;
    }

    public Iterator<String> propNames() {
        return props.fieldNames();
    }

    /**
     * @param configName
     * @return the config value as String, null if not found
     */
    public String get(String configName) {
        return props.get(configName).asText();
    }

    public int getInt(String configName) {
        return props.get(configName).asInt();
    }

    public boolean getBoolean(String configName) {
        return props.get(configName).asBoolean();
    }

    public double getDouble(String configName) {
        return props.get(configName).asDouble();
    }
}
