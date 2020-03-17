package clightning.apis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Optional;

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

        private Optional<String> data = Optional.of("");

        @JsonProperty("num_skipped")
        private Optional<Integer> numSkipped = Optional.of(0);
    }

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("bytes_used")
    private long bytesUsed;

    @JsonProperty("bytes_max")
    private long bytesMax;

    @JsonProperty("log")
    private Log[] logs;
}
