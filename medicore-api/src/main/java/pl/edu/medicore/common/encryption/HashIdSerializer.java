package pl.edu.medicore.common.encryption;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

@Component
@RequiredArgsConstructor
public class HashIdSerializer extends ValueSerializer<HashId> {
    private final IdObfuscatorService idObfuscator;

    @Override
    public void serialize(HashId hashId, JsonGenerator gen, SerializationContext ctxt) {
        if (hashId != null && hashId.value() != null) {
            gen.writeString(idObfuscator.encode(hashId.value()));
        } else {
            gen.writeNull();
        }
    }
}