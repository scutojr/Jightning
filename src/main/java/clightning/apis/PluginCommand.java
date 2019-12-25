package clightning.apis;

import java.util.HashMap;
import java.util.Map;

public class PluginCommand {
    private String command;
    private String parameter;

    private PluginCommand(String command) {
        this.command = command;
    }

    private PluginCommand setParameter(String parameter) {
        this.parameter = parameter;
        return this;
    }

    public String getCommand() {
        return command;
    }

    public Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("command", command);
        params.put("parameter", parameter);
        return params;
    }

    public static PluginCommand start(String path) {
        return new PluginCommand("start").setParameter(path);
    }

    public static PluginCommand stop(String pluginName) {
        return new PluginCommand("stop").setParameter(pluginName);
    }

    public static PluginCommand startDir(String path) {
        return new PluginCommand("startdir").setParameter(path);
    }

    public static PluginCommand rescan() {
        return new PluginCommand("rescan");
    }

    public static PluginCommand list() {
        return new PluginCommand("list");
    }
}
