package clightning.utils;

import clightning.JsonFormatException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.io.IOException;

public class JsonUtil {
    private static ObjectMapper mapper = new ObjectMapper().registerModule(
            new Jdk8Module()
    );

    public static <T> T convert(String data, Class<T> valueType) {
        try {
            return mapper.readValue(data, valueType);
        } catch (IOException e) {
            throw new JsonFormatException(e);
        }
    }

    public static <T> T convert(JsonNode data, Class<T> valueType) {
        try {
            return mapper.treeToValue(data, valueType);
        } catch (JsonProcessingException e) {
            throw new JsonFormatException(e);
        }
    }
}
