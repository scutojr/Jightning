package clightning.plugin;

import clightning.utils.JsonUtil;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.googlecode.jsonrpc4j.JsonRpcInterceptor;
import com.googlecode.jsonrpc4j.JsonRpcServer;
import lombok.Getter;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Class used for creating plugin to customized behaviour towards lightning daemon event and develop extra command
 * called directly from the command line.
 * <p>
 * In order to develop a plugin, user must inherit {@code Plugin} and annotated method with {@link Command},
 * {@link Hook}, {@link Subscribe}.
 * <p>
 * Plugin will run as a subprocess that are started by the main lightningd daemon and can interact with lightningd
 * in a variety of ways:
 * <ul>
 *     <li>
 *         Command line option passthrough allows plugins to register their own command line options that are exposed
 *         through lightningd so that only the main process needs to be configured.
 *     </li>
 *     <li>
 *         JSON-RPC command passthrough adds a way for plugins to add their own commands to the JSON-RPC interface.
 *         Methods annotated with {@link Command} will be treated as JSON-RPC command.
 *     </li>
 *     <li>
 *         Event stream subscriptions provide plugins with a push-based notification mechanism about events from
 *         the lightningd. Methods annotated with {@link Subscribe} will be treated as event subscription. The way
 *         handling event subscriptions is asynchronous. That is to say, lightning daemon will not rely on the result
 *         of event handling.
 *     </li>
 *     <li>
 *         Hooks are a primitive that allows plugins to be notified about internal events in lightningd and alter
 *         its behavior or inject custom behaviors. Methods annotated with {@link Hook} will be treated as hook.
 *         Hook handling is synchronous and lightning daemon will continue to run after hook handling is finished.
 *         Only one plugin may register for any hook topic at any point in time.
 *     </li>
 * </ul>
 * <p>
 * By convention, method for handling JSON-RPC command, event subscriptions, and hook must accept deserializable input
 * parameters and may return serializable object if it's not void. Currently, serialization/deserialization is driven
 * by Jackson.
 *
 * @see <a href=https://github.com/ElementsProject/lightning/blob/v0.7.3/doc/PLUGINS.md>Plugins</a>
 */
public abstract class Plugin {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String GET_MANIFEST = "getmanifest";
    private static final String INIT = "init";
    private static byte[] DELIMITER = "\n\n".getBytes();

    private InputStream in;
    private OutputStream out;

    private PluginProcessor.PluginMetaInfo metaInfo;
    private Manifest manifest = new Manifest();
    private Map<String, Method> commands = new HashMap<>();
    private Map<String, Method> subscriptions = new HashMap<>();
    private Map<String, Method> hooks = new HashMap<>();
    private JsonRpcServer server = new JsonRpcServer(this);

    protected ObjectMapper mapper;

    /**
     * Create a {@code Plugin} instance
     */
    public Plugin() {
        this(System.in, System.out);
    }

    /**
     * Create a {@code Plugin} instance
     *
     * @param in  inputStream for reading request
     * @param out outputStream for writing response
     */
    public Plugin(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
        mapper = JsonUtil.getMapper();
    }

    /**
     * Run the plugin
     *
     * @throws IOException
     */
    public void run() throws IOException {
        run(false);
    }

    /**
     * Run the plugin
     *
     * @param dynamic indicate if the plugin can be managed after lightningd has been started. Critical plugins
     *                that should not be stop should set it to false.
     * @throws IOException
     */
    public void run(boolean dynamic) throws IOException {
        String className = getClass().getCanonicalName();
        metaInfo = PluginProcessor.MetaStore.load().getPluginMetaInfo(className);
        if (Objects.isNull(metaInfo)) {
            throw new NullPointerException("PluginMetaInfo is not found for " + className);
        }
        generateManifest(dynamic);
        logger.info("start plugin with manifest " + mapper.writeValueAsString(manifest));
        handleRequests();
    }

    /**
     * Stop the plugin
     */
    public void stop() {
        try {
            in.close();
        } catch (IOException e) {
            logger.error("error while stop plugin", e);
        }
    }

    /**
     * Add an option of type string to the plugin.It must be called before {@link #run} is called.
     *
     * @param name        option name
     * @param default_    option default value
     * @param description option description
     */
    public void addOption(String name, String default_, String description) {
        manifest.addOption(name, default_, description, "string");
    }

    /**
     * Add an option of type int to the plugin.It must be called before {@link #run} is called.
     *
     * @param name        option name
     * @param default_    option default value
     * @param description option description
     */
    public void addOption(String name, Integer default_, String description) {
        manifest.addOption(name, default_, description, "int");
    }

    /**
     * Add an option of type bool to the plugin.It must be called before {@link #run} is called.
     *
     * @param name        option name
     * @param default_    option default value
     * @param description option description
     */
    public void addOption(String name, boolean default_, String description) {
        manifest.addOption(name, default_, description, "bool");
    }

    /**
     * Get the manifest of the plugin. It's called automatically on plugin startup.
     *
     * @return {@code Manifest}
     */
    @Command(name = GET_MANIFEST)
    public Manifest getManifest() {
        return manifest;
    }

    /**
     * Initialized the plugin. It's called right after {@link #getManifest()} is called.
     *
     * @param options
     * @param configuration
     * @return arbitrary data is ok. Lightning daemon will not check this return value.
     */
    protected abstract Object initialize(JsonNode options, JsonNode configuration);

    /**
     * Initialized the plugin. It's called right after {@link #getManifest()} is called.
     *
     * @param options
     * @param configuration
     * @return arbitrary data is ok. Lightning daemon will not check this return value.
     */
    @Command
    public Object init(JsonNode options, JsonNode configuration) {
        return initialize(options, configuration);
    }

    /**
     * Write info level log to lightning daemon
     *
     * @param message message to log
     */
    public void logInfo(String message) {
        log(message, "info");
    }

    /**
     * Write warn level log to lightning daemon
     *
     * @param message message to log
     */
    public void logWarn(String message) {
        log(message, "warn");
    }

    /**
     * Write log to lightning daemon
     *
     * @param message message to log
     * @param level   log level
     */
    public void log(String message, String level) {
        ObjectNode req = mapper.createObjectNode();
        ObjectNode params = mapper.createObjectNode();

        params.put("level", level);
        params.put("message", message);
        req.put("jsonrpc", "2.0");
        req.put("method", "log");
        req.replace("params", params);
        try {
            mapper.writeValue(out, req);
            out.write(DELIMITER);
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void reOrgParams(Method method, ObjectNode request) throws IOException {
        if (Objects.isNull(method)) {
            throw new RuntimeException("unknown method from request: " + request);
        }
        JsonNode paramsNode = request.get("params");
        Parameter[] parameters = method.getParameters();
        if (paramsNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) paramsNode;
            for (int i = arrayNode.size(); i < parameters.length; i++) {
                Parameter param = parameters[i];
                DefaultTo def = param.getAnnotation(DefaultTo.class);
                if (def == null) {
                    logger.error("not enough input argument for request "
                            + mapper.writeValueAsString(request)
                            + ".Please pass a current request or"
                            + "set a default value to the plugin method input parameter by @DefaultTo"
                    );
                    break;
                }
                String defValue = def.value();
                if (String.class.isAssignableFrom(param.getType())) {
                    defValue = "\"" + defValue + "\"";
                }
                arrayNode.add(mapper.readTree(defValue));
            }
        } else {
            PluginProcessor.MethodSignature signature = metaInfo.get(method.getName());
            if (Objects.isNull(signature)) {
                throw new NullPointerException(
                        "method signature is not found. It may be a bug and please turned to developer"
                );
            }
            ArrayNode arguments = mapper.createArrayNode();
            for (int i = 0; i < parameters.length; i++) {
                Parameter param = parameters[i];
                String paramName = signature.argName(i);
                DefaultTo def;
                if (paramsNode.has(paramName)) {
                    arguments.add(paramsNode.get(paramName));
                } else if ((def = param.getAnnotation(DefaultTo.class)) != null) {
                    String defValue = def.value();
                    if (String.class.isAssignableFrom(param.getType())) {
                        defValue = "\"" + defValue + "\"";
                    }
                    arguments.add(mapper.readTree(defValue));
                } else {
                    logger.error("not enough input argument for request "
                            + mapper.writeValueAsString(request)
                            + ".Please pass a current request or"
                            + "set a default value to the plugin method input parameter by @DefaultTo"
                    );
                    break;
                }
            }
            request.replace("params", arguments);
        }
    }

    private void transformCommand(JsonNode req) throws IOException {
        String methodName = req.get("method").asText();
        Method method = commands.get(methodName);
        ((ObjectNode) req).put("method", method.getName());
        reOrgParams(method, (ObjectNode) req);
    }

    private void transformSubscribe(JsonNode request) throws IOException {
        ObjectNode req = (ObjectNode) request;
        String topic = request.get("method").asText();
        Method method = subscriptions.get(topic);
        req.put("method", method.getName());
        reOrgParams(method, req);
    }

    private void transformHook(JsonNode request) throws IOException {
        ObjectNode req = (ObjectNode) request;
        String topic = req.get("method").asText();
        Method method = hooks.get(topic);
        req.put("method", method.getName());
        reOrgParams(method, req);
    }

    private void handleRequests() throws IOException {
        StdInWrapper input = new StdInWrapper(in);
        server.setInterceptorList(Arrays.asList(new JsonRpcInterceptor() {
            @SneakyThrows
            @Override
            public void preHandleJson(JsonNode jsonNode) {
                try {
                    if (jsonNode.has("id")) {
                        String methodName = jsonNode.get("method").asText();
                        if (commands.containsKey(methodName)) {
                            transformCommand(jsonNode);
                        } else {
                            transformHook(jsonNode);
                        }
                    } else {
                        transformSubscribe(jsonNode);
                    }
                } catch (IOException e) {
                    logger.error("error while transforming the request "
                                    + mapper.writeValueAsString(jsonNode)
                                    + " into internal format",
                            e
                    );
                    throw e;
                }
            }

            @Override
            public void preHandle(Object o, Method method, List<JsonNode> list) {

            }

            @Override
            public void postHandle(Object o, Method method, List<JsonNode> list, JsonNode jsonNode) {

            }

            @Override
            public void postHandleJson(JsonNode jsonNode) {

            }
        }));
        while (!input.isClosed()) {
            server.handleRequest(input, out); // a '\n' will be written to the end of every response
            out.write('\n');
            out.flush();
        }
    }

    private void registerCommand(Method method, Command command) {
        String name = command.name().equals("") ? method.getName() : command.name();
        String description = command.description();
        String longDescription = null;
        if (!command.longDescription().equals("")) {
            longDescription = command.longDescription();
        }

        // generate usage information
        PluginProcessor.MethodSignature signature = metaInfo.get(method.getName());
        Parameter[] parameters = method.getParameters();
        List<String> usage = new ArrayList<>();
        boolean foundAnnotated = false;
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            String paramName = signature.argName(i);
            if (param.isAnnotationPresent(DefaultTo.class)) {
                usage.add("[" + paramName + "]");
                foundAnnotated = true;
            } else {
                usage.add(paramName);
                if (foundAnnotated) {
                    throw new RuntimeException(
                            "all params must be annotated by DefaultTo after a DefaultTo annotated param."
                    );
                }
            }
        }
        if (commands.containsKey(name)) {
            throw new RuntimeException("command method has been registered: " + name);
        }
        commands.put(name, method);
        if (!name.equals(GET_MANIFEST) && !name.equals(INIT)) {
            manifest.addCommand(name, description, String.join(" ", usage), longDescription);
        }
    }

    private void registerSubscription(Method method, Subscribe subscribe) {
        String topic = subscribe.value().name();
        if (subscriptions.containsKey(topic)) {
            throw new RuntimeException("subscription method has been registered for topic: " + topic);
        }
        subscriptions.put(topic, method);
        manifest.addSubscription(topic);
    }

    private void registerHook(Method method, Hook hook) {
        Class rtype = method.getReturnType();
        if (rtype == Void.class) {
            throw new RuntimeException("wrong hook method signature");
        }
        String topic = hook.value().name();
        if (commands.containsKey(topic) || hooks.containsKey(topic)) {
            throw new RuntimeException("hook method has been registered for topic: " + topic);
        }
        hooks.put(topic, method);
        manifest.addHook(topic);
    }

    /**
     * Generate manifest information according to method registered to plugin.
     *
     * @param dynamic indicate if the plugin can be managed after lightningd has been started. Critical plugins
     *                that should not be stop should set it to false.
     */
    public void generateManifest(boolean dynamic) {
        Method[] methods = this.getClass().getMethods();
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                Class type = annotation.annotationType();
                if (type == Command.class) {
                    registerCommand(method, (Command) annotation);
                } else if (type == Subscribe.class) {
                    registerSubscription(method, (Subscribe) annotation);
                } else if (type == Hook.class) {
                    registerHook(method, (Hook) annotation);
                }
            }
        }
        manifest.dynamic = dynamic;
    }

    private class StdInWrapper extends InputStream {
        private int writeIndex = 0;
        private int readIndex = 0;
        private byte[] buffer = new byte[65535];

        private boolean closed;
        private InputStream in;

        public StdInWrapper(InputStream in) {
            this.in = in;
            closed = false;
        }

        private int nextDelimiter() {
            for (int i = readIndex; i <= writeIndex - DELIMITER.length; i++) {
                boolean found = true;
                for (int j = 0; j < DELIMITER.length; j++) {
                    if (buffer[i] != DELIMITER[j]) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    return i;
                }
            }
            return -1;
        }

        public boolean isClosed() {
            return closed;
        }

        @Override
        public int available() throws IOException {
            return in.available();
        }

        @Override
        public int read() throws IOException {
            throw new RuntimeException("not yet implemented");
        }

        @Override
        public int read(byte b[], int off, int len) throws IOException {
            int remaining = buffer.length - writeIndex;
            if (remaining <= 0 && readIndex >= writeIndex) {
                readIndex = writeIndex = 0;
                remaining = buffer.length;
            }
            int c = in.read(buffer, writeIndex, remaining);
            if (c == -1) {
                if (readIndex >= writeIndex) {
                    closed = true;
                    return c;
                }
            } else {
                writeIndex += c;
            }

            int d = nextDelimiter();
            int toRead = 0;
            if (d == -1) {
                toRead = Math.min(len, writeIndex - readIndex);
                System.arraycopy(buffer, readIndex, b, off, toRead);
                readIndex += toRead;
            } else {
                int src = readIndex;
                if (len < d - readIndex) {
                    toRead = len;
                    readIndex += len;
                } else {
                    toRead = d - readIndex;
                    readIndex = d + 2;
                }
                System.arraycopy(buffer, src, b, off, toRead);
            }

            String req = new String(Arrays.copyOfRange(b, off, off + toRead));
            return toRead;
        }
    }

    /**
     * Plugin manifest information
     */
    @Getter
    public class Manifest {
        @JsonProperty("rpcmethods")
        private List<Map> commands;
        private List<Option> options;
        private List<String> subscriptions;
        private List<String> hooks;
        private boolean dynamic;

        public Manifest() {
            commands = new ArrayList<>();
            options = new ArrayList<>();
            subscriptions = new ArrayList<>();
            hooks = new ArrayList<>();
            dynamic = false;
        }

        void addCommand(String name, String description, String usage, String longDescription) {
            Map<String, String> method = new HashMap<>();
            method.put("name", name);
            method.put("description", description);
            method.put("usage", usage);
            if (Objects.nonNull(longDescription)) {
                method.put("long_description", longDescription);
            }
            commands.add(method);
        }

        void addOption(String name, Object default_, String description, String type) {
            Option option = new Option();
            option.name = name;
            option.default_ = default_;
            option.description = description;
            option.type = type;

            options.add(option);
        }

        void addSubscription(String topic) {
            subscriptions.add(topic);
        }

        void addHook(String topic) {
            hooks.add(topic);
        }
    }

    /**
     * Plugin option
     */
    @Getter
    public class Option {
        String name;
        String type;

        @JsonProperty("default")
        Object default_;

        String description;
    }
}
