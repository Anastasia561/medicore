package pl.edu.medicore.common.encryption;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
class IdObfuscatorServiceImpl implements IdObfuscatorService {
    private static final int MIN_HASH_LENGTH = 8;

    private final Hashids hashids;

    public IdObfuscatorServiceImpl(@Value("${app.salt}") String salt) {
        this.hashids = new Hashids(salt, MIN_HASH_LENGTH);
    }

    @Override
    public String encode(Long id) {
        if (id == null) return null;
        return hashids.encode(id);
    }

    @Override
    public Long decode(String hash) {
        if (hash == null || hash.isEmpty()) return null;
        long[] decoded = hashids.decode(hash);
        if (decoded.length == 0) {
            throw new IllegalArgumentException("Invalid obscured ID format.");
        }
        return decoded[0];
    }
}