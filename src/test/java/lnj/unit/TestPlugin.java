package lnj.unit;

import clightning.plugin.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import org.junit.Test;

public class TestPlugin {
    public static class Handler implements BaseHandler {
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

    @Test
    public void testPlugin() throws JsonProcessingException {
        Plugin plugin = new Plugin(new Handler());
        plugin.run();
    }
}
