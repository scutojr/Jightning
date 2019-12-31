package clightning.apis.optional;

public class ListPeersParams extends OptionalParams {
    public ListPeersParams setId(String id) {
        params.put("id", id);
        return this;
    }

    public ListPeersParams setLevel(LogLevel level) {
        params.put("level", level.name());
        return this;
    }
}
