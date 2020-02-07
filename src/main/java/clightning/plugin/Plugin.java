package clightning.plugin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.googlecode.jsonrpc4j.JsonRpcInterceptor;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcServer;
import lombok.Getter;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class Plugin {
    private static final String GET_MANIFEST = "getmanifest";
    private static final String INIT = "init";

    private InputStream in;
    private OutputStream out;

    private Manifest manifest = new Manifest();
    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, Method> methods = new HashMap<>();
    private JsonRpcServer server = new JsonRpcServer(this);

    public Plugin() {
        this(System.in, System.out);
    }

    public Plugin(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    public void run() throws IOException {
        generateManifest();
//        System.out.println(mapper.writeValueAsString(manifest));
        handleRequests();
    }

    @RpcMethod(name = GET_MANIFEST)
    public void getManifest() throws IOException {
        mapper.writeValue(System.out, manifest);
    }

    @RpcMethod
    public Object init() {
        return null;
    }

    private void transformAndInject(JsonNode req) throws IOException {
        //TODO: check null
        String methodName = req.get("method").asText();
        ObjectNode params = (ObjectNode) req.get("params");
        Method method = methods.get(methodName);
        ((ObjectNode) req).replace("method", new TextNode(method.getName()));
        for (Parameter param : method.getParameters()) {
            //TODO: get param name by other way
            Annotation a = param.getAnnotation(JsonRpcParam.class);
            String paramName = ((JsonRpcParam) a).value();
//            String paramName = param.getName();
//            if (Objects.nonNull(a)) {
//                String n = ((JsonRpcParam) a).value();
//                if (params.has(n)) {
//                    JsonNode p = params.get(n);
//                    params.remove(n);
//                    params.set(param.getName(), p);
//                }
//            }

            a = param.getAnnotation(DefaultTo.class);
            if (Objects.nonNull(a) && !params.has(paramName)) {
                String defaultValue = ((DefaultTo) a).value();
                JsonNode node;
                if (String.class.isAssignableFrom(param.getType())) {
                    defaultValue = "\"" + defaultValue + "\"";
                }
                node = mapper.readTree(defaultValue);
                params.replace(paramName, node);
            }
        }
    }

    private void handleRequests() throws IOException {
        InputStream inputWrapper = new StdInWrapper(in);
        server.setInterceptorList(Arrays.asList(new JsonRpcInterceptor() {
            @Override
            public void preHandleJson(JsonNode jsonNode) {
                try {
                    transformAndInject(jsonNode);
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
        while (true) {
            server.handleRequest(inputWrapper, out);
        }
    }

    private void handleRpcMethod(Method method, RpcMethod annotation) {
        String name = annotation.name().equals("") ? method.getName() : annotation.name();
        String description = annotation.description();
        String longDescription = null;
        if (!annotation.longDescription().equals("")) {
            longDescription = annotation.longDescription();
        }

        // generate usage information
        List<String> usage = new ArrayList<>();
        boolean foundAnnotated = false;
        for (Parameter param : method.getParameters()) {
            String paramName = param.isAnnotationPresent(JsonRpcParam.class) ?
                    param.getAnnotation(JsonRpcParam.class).value() :
                    param.getName();
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
        methods.put(name, method);
        manifest.addMethod(name, description, String.join(" ", usage), longDescription);
    }

    private void handleSubscription(Method method, Subscribe annotation) {
    }

    public JsonNode generateManifest() {
        Method[] methods = this.getClass().getMethods();
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                Class type = annotation.annotationType();
                if (type == RpcMethod.class) {
                    handleRpcMethod(method, (RpcMethod) annotation);
                } else if (type == Subscribe.class) {
                    handleSubscription(method, (Subscribe) annotation);
                }
            }
        }
        return null;
    }

    private static class StdInWrapper extends InputStream {
        private int writeIndex = 0;
        private int readIndex = 0;
        private byte[] buffer = new byte[65535];
        private byte[] delimiter = "\n\n".getBytes();

        private InputStream in;

        public StdInWrapper(InputStream in) {
            this.in = in;
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
        public Manifest() {
            methods = new ArrayList<>();
        }

        @JsonProperty("rpcmethods")
        private List<Map> methods;

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
    }
}
