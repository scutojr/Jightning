package clightning.utils;

import clightning.JsonFormatException;
import clightning.apis.Output;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.io.IOException;

/**
 * Utility for json operation
 */
public class JsonUtil {
    public static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new Jdk8Module());
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        mapper.configure(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM, true);

        SimpleModule m1 = new SimpleModule();
        m1.addDeserializer(Output[].class, new Output.Deserializer());
        mapper.registerModule(m1);
    }

    /**
     * Deserialize the {@code data} to object of type {@code valueType}
     *
     * @param data      object to be deserialized
     * @param valueType class type of the return value
     * @param <T>
     * @return the deserialization result
     */
    public static <T> T convert(String data, Class<T> valueType) {
        try {
            return mapper.readValue(data, valueType);
        } catch (IOException e) {
            throw new JsonFormatException(e);
        }
    }

    /**
     * Deserialize the {@code data} to object of type {@code valueType}
     *
     * @param data      object to be deserialized
     * @param valueType class type of the return value
     * @param <T>
     * @return the deserialization result
     */
    public static <T> T convert(JsonNode data, Class<T> valueType) {
        try {
            return mapper.treeToValue(data, valueType);
        } catch (JsonProcessingException e) {
            throw new JsonFormatException(e);
        }
    }

    /**
     * Get an ObjectMapper which has been configured for usage
     *
     * @return ObjectMapper instance
     */
    public static ObjectMapper getMapper() {
        return mapper;
    }
}
