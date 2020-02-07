package lnj.unit;

import clightning.plugin.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import lombok.Data;
import org.junit.Test;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class TestPlugin {
    public static class ComplexPlugin extends Plugin {
        @RpcMethod(description = "desc")
        public void method1(
                @JsonRpcParam("p1")
                        String p1,
                @JsonRpcParam("px")
                @DefaultTo("jljv") String px,
                @JsonRpcParam("p2")
                @DefaultTo("xxyy") String p2) {
        }

        @RpcMethod(
                name = "method2-xxyy",
                description = "desc",
                longDescription = "xxxxx"
        )
        public void method2(@JsonRpcParam("p1") String p1, @JsonRpcParam("px") String px, @JsonRpcParam("p2") @DefaultTo("xxyy") String p2) {
        }

        @Override
        public Object init() {
            return null;
        }
    }

    @Data
    public static class P {
        private String name;
        private String age;
    }

    public static class SimplePlugin extends Plugin {
        public SimplePlugin(InputStream in, OutputStream out) {
            super(in, out);
        }

        @RpcMethod
        public void method1(@JsonRpcParam("p1") int p1, @JsonRpcParam("p2") String p2) {
            System.out.println("method1 " + p1 + " " + p2);
        }

        @RpcMethod
        public void method2() {
            System.out.println("method2");
        }

        @RpcMethod(name = "method_3")
        public void method3(
                @JsonRpcParam("p1")
                        int p1,
                @JsonRpcParam("p2")
                @DefaultTo("{\"name\": \"haldjf\", \"age\": 123}")
                        P p2,
                @JsonRpcParam("p3")
                @DefaultTo("hehehe")
                        String p3
        ) {
            System.out.println("method3: " + p2.name + " " + p2.age);
        }
    }

    @Test
    public void xx() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode hehe = mapper.readTree("12345");
        System.out.println(hehe.asInt() + "  " + hehe.toString());
    }

    @Test
    public void testPlugin() throws IOException {
        String[] reqs = new String[]{
                "{\"method\": \"method1\", \"params\": {\"p1\": 123, \"p2\": \"ajldfe\"}, \"id\": 1}",
                "{\"method\": \"method_3\", \"params\": {\"p1\": 123}, \"id\": 2}",
//                "{\"method\": \"method2\", \"params\": {}, \"id\": 4}",
//                "{\"method\": \"method_3\", \"params\": {\"p1\": 123}, \"id\": 5}"
        };

        byte[] bytes = String.join("\n\n", reqs).getBytes();

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);

        Plugin plugin = new SimplePlugin(input, System.out);
        plugin.run();
    }
}
