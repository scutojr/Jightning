package clightning.apis.optional;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Based class for api optional parameters
 */
@JsonSerialize(using = OptionalParams.Serializer.class)
@JsonDeserialize(using = OptionalParams.Deserializer.class)
public class OptionalParams {
    protected Map<String, Object> params = new HashMap<>();

    public Map<String, Object> dump() {
        return params;
    }

    static class Serializer extends JsonSerializer {
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            OptionalParams params = (OptionalParams) value;
            gen.writeStartObject();
            gen.writeStringField("class", value.getClass().getName());
            gen.writeObjectField("obj", params.params);
            gen.writeEndObject();
        }
    }

    static class Deserializer extends JsonDeserializer {
        @SneakyThrows
        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) {
            ObjectCodec codec = p.getCodec();
            JsonNode nodes = codec.readTree(p);
            String className = nodes.get("class").asText();
            OptionalParams params = (OptionalParams) Class.forName(className).newInstance();
            params.params = codec.treeToValue(nodes.get("obj"), Map.class);
            return params;
        }
    }
}
