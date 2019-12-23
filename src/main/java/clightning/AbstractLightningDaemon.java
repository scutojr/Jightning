package clightning;

import java.io.IOException;
import java.util.Map;

public interface AbstractLightningDaemon {
    public <T> T execute(String method, Map params, Class<T> valueType) throws IOException;

    public <T> T execute(String method, Class<T> valueType) throws IOException;
}
