package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

/**
 * "created_at": "1577091960.690291352",
 * "bytes_used": 1154310,
 * "bytes_max": 20971520,
 * "log": [
 */
@Data
public class LogResult {
    @Data
    public static class Log {
        private String type;
        private String time;
        private String source;
        private String log;

        // TODO: data and numSkipped is not always existed. redesign it.
        private String data = "";
        @JsonSetter("num_skipped")
        private int numSkipped = 0;
    }

    @JsonSetter("created_at")
    private String createdAt;

    @JsonSetter("bytes_used")
    private long bytesUsed;

    @JsonSetter("bytes_max")
    private long bytesMax;

    @JsonSetter("log")
    private Log[] logs;
}
