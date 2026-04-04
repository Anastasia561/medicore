package pl.edu.medicore.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.edu.medicore.config.properties.S3Properties;
import pl.edu.medicore.exception.FileNotFoundException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@Component
@RequiredArgsConstructor
public class S3Utils {
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public void checkObject(String key) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(key)
                    .build();
            s3Client.headObject(request);
        } catch (NoSuchKeyException e) {
            throw new FileNotFoundException("File not found");
        }
    }

    public String buildKey(Long testId) {
        return s3Properties.getFolderName() + "/" +
                testId + "/" +
                s3Properties.getFileName();
    }
}
