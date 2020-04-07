package clightning.apis;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Plugin command builder.
 */
public class PluginCommand {
    private String command;
    private String parameter;
    private Object value;

    private PluginCommand(String command) {
        this.command = command;
    }

    private PluginCommand setParameter(String name, Object value) {
        this.parameter = name;
        this.value = value;
        return this;
    }

    public Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("subcommand", command);
        if (Objects.nonNull(parameter)) {
            params.put(parameter, value);
        }
        return params;
    }

    public String getCommand() {
        return command;
    }

    public static PluginCommand start(String path) {
        return new PluginCommand("start").setParameter("plugin", path);
    }

    public static PluginCommand stop(String pluginName) {
        return new PluginCommand("stop").setParameter("plugin", pluginName);
    }

    public static PluginCommand startDir(String path) {
        return new PluginCommand("startdir").setParameter("directory", path);
    }

    public static PluginCommand rescan() {
        return new PluginCommand("rescan");
    }

    public static PluginCommand list() {
        return new PluginCommand("list");
    }
}
