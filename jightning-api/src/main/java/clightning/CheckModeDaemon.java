package clightning;

import clightning.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Map;

public class CheckModeDaemon implements AbstractLightningDaemon {

    private ObjectMapper mapper;
    private ObjectNode req;

    public CheckModeDaemon() {
        mapper = JsonUtil.getMapper();
    }

    private ObjectNode createRequest(String method, Map params) {
        ObjectNode request = mapper.createObjectNode();
        request.put("method", method);
        request.replace("params", mapper.convertValue(params, JsonNode.class));
        return request;
    }

    public ObjectNode lastRequest() {
        return req;
    }

    @Override
    public synchronized <T> T execute(String method, Map params, Class<T> valueType) {
        req = createRequest(method, params);
        throw new ShortOutException("short out!");
    }

    @Override
    public synchronized <T> T execute(String method, Class<T> valueType) {
        req = createRequest(method, new HashMap());
        throw new ShortOutException("short out!");
    }
}
