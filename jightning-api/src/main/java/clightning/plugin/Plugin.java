package clightning.plugin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.primitives.UnsignedInteger;
import com.googlecode.jsonrpc4j.JsonRpcInterceptor;
import com.googlecode.jsonrpc4j.JsonRpcServer;
import lombok.Getter;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;


// TODO: add log method that will redirect to the lightning daemon
public abstract class Plugin {
    private static final String GET_MANIFEST = "getmanifest";
    private static final String INIT = "init";
    private static byte[] DELIMITER = "\n\n".getBytes();

    private InputStream in;
    private OutputStream out;

    private PluginProcessor.PluginMetaInfo metaInfo;
    private Manifest manifest = new Manifest();
    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, Method> commands = new HashMap<>();
    private Map<String, Method> subscriptions = new HashMap<>();
    private Map<String, Method> hooks = new HashMap<>();
    private JsonRpcServer server = new JsonRpcServer(this);

    public Plugin() {
        this(System.in, System.out);
    }

    public Plugin(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    public void run() throws IOException {
        run(false);
    }

    public void run(boolean dynamic) throws IOException {
        String className = getClass().getCanonicalName();
        metaInfo = PluginProcessor.MetaStore.load().getPluginMetaInfo(className);
        if (Objects.isNull(metaInfo)) {
            throw new NullPointerException("PluginMetaInfo is not found for " + className);
        }
        generateManifest(dynamic);
        // TODO: log debug level
        handleRequests();
    }

    public void stop() {
        try {
            in.close();
        } catch (IOException e) {
            // TODO: log here
            e.printStackTrace();
        }
    }

    public void addOption(String name, String default_, String description) {
        manifest.addOption(name, default_, description, "string");
    }

    public void addOption(String name, Integer default_, String description) {
        // TODO: integers ?
        manifest.addOption(name, default_, description, "integers");
    }

    public void addOption(String name, UnsignedInteger default_, String description) {
        // TODO: unsigned integers?
        manifest.addOption(name, default_, description, "unsigned integers");
    }

    public void addOption(String name, boolean default_, String description) {
        manifest.addOption(name, default_, description, "bool");
    }

    @Command(name = GET_MANIFEST)
    public Manifest getManifest() {
        return manifest;
    }

    protected abstract Object initialize(JsonNode options, JsonNode configuration);

    @Command
    public Object init(JsonNode options, JsonNode configuration) {
        return initialize(options, configuration);
    }

    private void reOrgParams(Method method, ObjectNode request) throws IOException {
        JsonNode paramsNode = request.get("params");
        Parameter[] parameters = method.getParameters();
        if (paramsNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) paramsNode;
            for (int i = arrayNode.size(); i < parameters.length; i++) {
                Parameter param = parameters[i];
                DefaultTo def = param.getAnnotation(DefaultTo.class);
                if (def == null) {
                    // TODO: log error here
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
                    // TODO: log error here
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
        // TODO: validate the topic and raise exception
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
                    //TODO log here
                    e.printStackTrace();
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
        for(int i =0;i< parameters.length;i++) {
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
        // TODO: validate the input parameter and return value of the hook handler method
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

    public void generateManifest(boolean dynamic) {
        // TODO: only support public method?
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
            int d = nextDelimiter();
            //TODO
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

    @Getter
    class Manifest {
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

    @Getter
    class Option {
        String name;
        String type;

        @JsonProperty("default")
        Object default_;

        String description;
    }
}
