package pl.edu.medicore.common.encryption;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class HashidToHashIdConverter implements Converter<String, HashId> {
    private final IdObfuscatorService idObfuscator;

    @Override
    public HashId convert(String source) {
        try {
            return HashId.of(idObfuscator.decode(source));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ID parameter provided.");
        }
    }
}