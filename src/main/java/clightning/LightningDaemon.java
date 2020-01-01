package clightning;

import clightning.apis.LightningClient;
import clightning.apis.response.LightningDaemonInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.google.common.base.Preconditions.*;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.*;
import java.net.Socket;
import java.rmi.Remote;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class LightningDaemon implements AbstractLightningDaemon {
    private static Map EMPTY_PARAMS = new HashMap();
    private AtomicInteger id = new AtomicInteger();
    private ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());
    private File udPath;
    private byte[] buffer = new byte[65535];

    public LightningDaemon() throws IOException {
        udPath = new File(getUnixDomainPath());
    }

    private String getUnixDomainPath() throws IOException {
        Process p = Runtime.getRuntime().exec("lightning-cli listconfigs");
        JsonNode configs = mapper.readTree(p.getInputStream());
        JsonNode lightningDir = configs.get("lightning-dir");
        JsonNode rpcFile = configs.get("rpc-file");
        checkNotNull(lightningDir);
        checkNotNull(rpcFile);
        return String.format("%s/%s", lightningDir.asText(), rpcFile.asText());
    }

    private JsonNode createRequest(String method, Map params) {
        JsonNodeFactory nodeFactory = mapper.getNodeFactory();
        ObjectNode request = nodeFactory.objectNode();
        request.put("method", method);
        request.put("params", mapper.convertValue(params, JsonNode.class));
        request.put("id", id.incrementAndGet());
        return request;
    }

    private void sendRequest(JsonNode request, OutputStream out) throws IOException {
        mapper.writeValue(out, request);
    }

    private int lastIndexOf(byte[] array, int len, byte[] pattern) {
        int pos = array.length - pattern.length;
        while (pos >= 0) {
            boolean flag = true;
            for (int i = 0; i < pattern.length; i++) {
                if (array[pos + i] != pattern[i]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                return pos;
            }
            pos -= 1;
        }
        return -1;
    }

    private JsonNode waitForResponse(InputStream in) throws IOException {
        JsonNode response;
        byte[] tmpBuf = buffer;
        byte[] delimiter = "\n\n".getBytes();
        int pos = 0, remaining = tmpBuf.length;
        while (true) {
            remaining -= in.read(tmpBuf, pos, remaining);
            pos = tmpBuf.length - remaining;
            int i = lastIndexOf(tmpBuf, pos, delimiter);
            if (i >= 0) {
                response = mapper.readTree(Arrays.copyOf(tmpBuf, i));
                break;
            } else {
                if (remaining <= 0) {
                    tmpBuf = Arrays.copyOf(tmpBuf, 2 * tmpBuf.length);
                    remaining = tmpBuf.length - pos;
                }
            }
        }
        return response;
    }

    private void handleResponse(JsonNode response) {
        // non json response
        // has error
        // result field is null or not available
    }

    /**
     * TODO: handle the RemoteException
     *
     * @param method
     * @param params
     * @param valueType
     * @param <T>
     * @return
     */
    @Override
    public synchronized <T> T execute(String method, Map params, Class<T> valueType) {
        try {
            JsonNode req = createRequest(method, params);
            JsonNode rsp;
            Socket socket = AFUNIXSocket.newInstance();
            socket.connect(new AFUNIXSocketAddress(udPath));
            sendRequest(req, socket.getOutputStream());
            rsp = waitForResponse(socket.getInputStream());
            if (rsp.has("error")) {
                JsonNode error = rsp.get("error");
                int code = error.get("code").asInt();
                String message = error.get("message").asText();
                throw new RemoteException(message, code);
            }
            socket.close();
            return mapper.treeToValue(rsp.get("result"), valueType);
        } catch (IOException e) {
            throw new RemoteException(e);
        }
    }

    @Override
    public synchronized <T> T execute(String method, Class<T> valueType) {
        return execute(method, EMPTY_PARAMS, valueType);
    }

    public LightningClient getLightningClient() {
        return new LightningClient(this);
    }
}
