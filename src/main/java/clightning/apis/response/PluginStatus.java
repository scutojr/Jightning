package clightning.apis.response;

import lombok.Getter;

import java.io.File;
import java.util.*;

public class PluginStatus {

    private Map<String, Plugin> plugins = new HashMap<>();

    public void updatePlugin(String nameOrPath, boolean active) {
        Plugin plugin = new Plugin(nameOrPath, active);
        plugins.put(plugin.getName(), plugin);
    }

    public boolean isActive(String nameOrPath) {
        String name = Plugin.extractName(nameOrPath);
        return plugins.containsKey(name) ? plugins.get(name).active : false;
    }

    public Collection<Plugin> plugins() {
        return plugins.values();
    }

    @Getter
    public static class Plugin {
        private String path;
        private String name;
        private boolean active;

        Plugin(String nameOrPath, boolean active) {
            path = nameOrPath;
            name = extractName(nameOrPath);
            this.active = active;
        }

        public static String extractName(String nameOrPath) {
            return new File(nameOrPath).getName();
        }
    }
}
