package pl.edu.medicore.common.encryption;

public record HashId(Long value) {

    public static HashId of(Long value) {
        return value == null ? null : new HashId(value);
    }
}