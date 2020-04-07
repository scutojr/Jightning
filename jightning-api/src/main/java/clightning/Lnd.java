package clightning;

import java.util.Map;

public interface Lnd {
    <T> T execute(String method, Map params, Class<T> valueType);

    <T> T execute(String method, Class<T> valueType);
}
