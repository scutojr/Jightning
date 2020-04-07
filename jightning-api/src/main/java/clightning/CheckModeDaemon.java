package clightning;

import clightning.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code CheckModeDaemon} is used to support {@link clightning.apis.BasedUtility#check}.
 * It's a hacking implementation to the normal lightning daemon command execution procedure.
 */
public class CheckModeDaemon implements Lnd {

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

    /**
     * Execute the lightning daemon command.If the lightning command is executed normally,
     * {@code ShortOutException} will be thrown.
     *
     * @param method    lightning daemon command name
     * @param params    params required to call the command
     * @param valueType the return value type
     * @param <T>       anything that can be deserialized by Jackson
     * @return
     * @throws ShortOutException it's normal to throw this exception
     */
    @Override
    public synchronized <T> T execute(String method, Map params, Class<T> valueType) {
        req = createRequest(method, params);
        throw new ShortOutException("short out!");
    }

    /**
     * Execute the lightning daemon command.If the lightning command is executed normally,
     * {@code ShortOutException} will be thrown.
     *
     * @param method    lightning daemon command name
     * @param valueType the return value type
     * @param <T>       anything that can be deserialized by Jackson
     * @return
     * @throws ShortOutException it's normal to throw this exception
     */
    @Override
    public synchronized <T> T execute(String method, Class<T> valueType) {
        req = createRequest(method, new HashMap());
        throw new ShortOutException("short out!");
    }
}
