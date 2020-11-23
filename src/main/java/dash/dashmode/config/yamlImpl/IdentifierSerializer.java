package dash.dashmode.config.yamlImpl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class IdentifierSerializer extends StdDeserializer<Identifier> {

    public IdentifierSerializer() {
        this(null);
    }

    public IdentifierSerializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Identifier deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.readValueAs(String.class);
        return new Identifier(value);
    }
}
