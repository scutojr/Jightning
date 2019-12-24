package clightning.apis.optional;

import java.util.HashMap;
import java.util.Map;

public class OptionalParams {
    protected Map<String, Object> params = new HashMap<>();

    public Map<String, Object> dump() {
        return params;
    }
}
