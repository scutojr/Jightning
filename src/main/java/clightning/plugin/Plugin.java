package clightning.plugin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.primitives.UnsignedInteger;
import com.googlecode.jsonrpc4j.JsonRpcInterceptor;
import com.googlecode.jsonrpc4j.JsonRpcServer;
import lombok.Getter;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public abstract class Plugin {
    private static final String GET_MANIFEST = "getmanifest";
    private static final String INIT = "init";

    private InputStream in;
    private OutputStream out;

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
        generateManifest(dynamic);
        // TODO: log debug level
        System.out.println(mapper.writeValueAsString(manifest));
        handleRequests();
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
    public void getManifest() throws IOException {
        mapper.writeValue(System.out, manifest);
    }

    protected abstract Object initialize();

    @Command
    public Object init() {
        return init();
    }

    private void transformCommand(JsonNode req) throws IOException {
        //TODO: check null
        String methodName = req.get("method").asText();
        Method method = commands.get(methodName);
        Parameter[] parameters = method.getParameters();

        JsonNode params = req.get("params");
        ((ObjectNode) req).replace("method", new TextNode(method.getName()));

        if (params.isArray()) {
            ArrayNode arrayNode = (ArrayNode) params;
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
            ArrayNode arguments = mapper.createArrayNode();
            for (Parameter param : parameters) {
                String paramName = param.getName();
                DefaultTo def;
                if (params.has(paramName)) {
                    arguments.add(params.get(paramName));
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
            ((ObjectNode) req).replace("params", arguments);
        }
    }

    private void transformSubscribe(JsonNode request) {
        // TODO: validate the topic and raise exception
        ObjectNode req = (ObjectNode) request;
        String topic = request.get("method").asText();
        String internalName = subscriptions.get(topic).getName();
        req.replace("method", TextNode.valueOf(internalName));
        // TODO: what if params is null or does not exist?
        JsonNode params = req.get("params");
        if (!params.isArray()) {
            req.replace("params", mapper.createArrayNode().add(params));
        }
    }

    private void transformHook(JsonNode request) {
        ObjectNode req = (ObjectNode) request;
        String topic = req.get("method").asText();
        Method method = hooks.get(topic);
        req.replace("method", TextNode.valueOf(method.getName()));
        // TODO: what if params is null or does not exist?
        JsonNode params = req.get("params");
        if (!params.isArray()) {
            req.replace("params", mapper.createArrayNode().add(params));
        }
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
            server.handleRequest(input, out);
        }
    }

    private void registerCommand(Method method, Command cmmand) {
        String name = cmmand.name().equals("") ? method.getName() : cmmand.name();
        String description = cmmand.description();
        String longDescription = null;
        if (!cmmand.longDescription().equals("")) {
            longDescription = cmmand.longDescription();
        }

        // generate usage information
        List<String> usage = new ArrayList<>();
        boolean foundAnnotated = false;
        for (Parameter param : method.getParameters()) {
            String paramName = param.getName();
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
        manifest.addMethod(name, description, String.join(" ", usage), longDescription);
    }

    private void registerSubscription(Method method, Subscribe subscribe) {
        if (method.getParameterCount() != 1 || !method.getParameterTypes()[0].isAssignableFrom(JsonNode.class)) {
            String msg = "signature of the subscription method only accept one parameter of type JsonNode";
            throw new RuntimeException(msg);
        }
        String topic = subscribe.value().name();
        if (subscriptions.containsKey(topic)) {
            throw new RuntimeException("subscription method has been registered: " + topic);
        }
        subscriptions.put(topic, method);
        manifest.addSubscription(topic);
    }

    private void registerHook(Method method, Hook hook) {
        // TODO: validate the input parameter and return value of the hook handler method
        int c = method.getParameterCount();
        Class[] ptypes = method.getParameterTypes();
        Class rtype = method.getReturnType();
        if (c != 1 || !ptypes[0].isAssignableFrom(JsonNode.class) || rtype == Void.class) {
            throw new RuntimeException("wrong hook method signature");
        }
        String topic = hook.value().name();
        if (commands.containsKey(topic) || hooks.containsKey(topic)) {
            throw new RuntimeException("hook method has been registered: " + topic);
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

    private static class StdInWrapper extends InputStream {
        private int writeIndex = 0;
        private int readIndex = 0;
        private byte[] buffer = new byte[65535];
        private byte[] delimiter = "\n\n".getBytes();

        private boolean closed;
        private InputStream in;

        public StdInWrapper(InputStream in) {
            this.in = in;
            closed = false;
        }

        private int nextDelimiter() {
            for (int i = readIndex; i <= writeIndex - delimiter.length; i++) {
                boolean found = true;
                for (int j = 0; j < delimiter.length; j++) {
                    if (buffer[i] != delimiter[j]) {
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
            return toRead;
        }
    }

    @Getter
    class Manifest {
        @JsonProperty("rpcmethods")
        private List<Map> methods;
        private List<Option> options;
        private List<String> subscriptions;
        private List<String> hooks;
        private boolean dynamic;

        public Manifest() {
            methods = new ArrayList<>();
            options = new ArrayList<>();
            subscriptions = new ArrayList<>();
            hooks = new ArrayList<>();
            dynamic = false;
        }

        void addMethod(String name, String description, String usage, String longDescription) {
            Map<String, String> method = new HashMap<>();
            method.put("name", name);
            method.put("description", description);
            method.put("usage", usage);
            if (Objects.nonNull(longDescription)) {
                method.put("long_description", longDescription);
            }
            methods.add(method);
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
