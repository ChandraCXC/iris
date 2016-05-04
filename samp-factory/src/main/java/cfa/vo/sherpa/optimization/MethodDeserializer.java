package cfa.vo.sherpa.optimization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class MethodDeserializer extends JsonDeserializer<Method> {

    @Override
    public Method deserialize(JsonParser jp, DeserializationContext deserializationContext)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String name = node.asText();
        return OptimizationMethod.valueOf(name);
    }
}
