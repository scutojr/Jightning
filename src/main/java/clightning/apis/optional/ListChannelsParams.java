package clightning.apis.optional;

public class ListChannelsParams extends OptionalParams {
    public ListChannelsParams setShortChannelId(String shortChannelId) {
        params.put("short_channel_id", shortChannelId);
        return this;
    }

    public ListChannelsParams setSource(String source) {
        params.put("source", source);
        return this;
    }
}
