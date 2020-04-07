package clightning.apis.optional;

/**
 * Extra optional parameters for {@link clightning.apis.BasedChannel#listChannels(ListChannelsParams)}
 */
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
