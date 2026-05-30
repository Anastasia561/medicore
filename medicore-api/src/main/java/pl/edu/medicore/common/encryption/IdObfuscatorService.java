package pl.edu.medicore.common.encryption;

public interface IdObfuscatorService {
    String encode(Long id);

    Long decode(String hash);
}
