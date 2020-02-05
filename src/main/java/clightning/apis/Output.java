package clightning.apis;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@JsonSerialize(using = Output.Serializer.class)
@JsonDeserialize(using = Output.Deserializer.class)
public class Output {
    private String address;
    private long amount;
    private String display;

    public Output(String address, long satoshi) {
        this.address = address;
        amount = satoshi;
    }

    public Output(String address, String display) {
        this.address = address;
        this.display = display;
        assert display.endsWith("msat");
        String value = display.substring(0, display.length() - "msat".length());
        amount = Long.parseLong(value) / 1000;
    }

    public static class Serializer extends JsonSerializer {

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            Output output = (Output) value;
            gen.writeStartObject();
            if (Objects.nonNull(output.display)) {
                gen.writeStringField(output.address, output.display);
            } else {
                gen.writeNumberField(output.address, output.amount);
            }
            gen.writeEndObject();
        }
    }

    public static class Deserializer extends JsonDeserializer<Output[]> {
        @Override
        public Output[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            List<Output> array = new ArrayList<>();

            while (!p.isClosed()) {
                JsonToken token = p.nextToken();
                if (JsonToken.END_OBJECT.equals(token)) {
                    break;
                } else if (JsonToken.FIELD_NAME.equals(token)) {
                    String address = p.currentName();
                    token = p.nextToken();
                    Output output;
                    if (token.isNumeric()) {
                        output = new Output(address, p.getValueAsLong());
                    } else {
                        output = new Output(address, p.getValueAsString());
                    }
                    array.add(output);
                }
            }
            Output[] res = array.toArray(new Output[array.size()]);
            return res;
        }
    }
}
