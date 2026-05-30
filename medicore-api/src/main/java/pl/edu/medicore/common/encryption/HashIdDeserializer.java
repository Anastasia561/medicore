package pl.edu.medicore.common.encryption;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

@Component
@RequiredArgsConstructor
public class HashIdDeserializer extends ValueDeserializer<HashId> {
    private final IdObfuscatorServiceImpl idObfuscator;

    @Override
    public HashId deserialize(JsonParser p, DeserializationContext ctxt){
        String hash = p.getValueAsString();
        if (hash == null || hash.isEmpty()) return null;

        try {
            return HashId.of(idObfuscator.decode(hash));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ID format", e);
        }
    }
}