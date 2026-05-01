package pl.edu.medicore.infrastructure.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.medicore.config.properties.S3Properties;
import pl.edu.medicore.infrastructure.storage.S3Utils;
import pl.edu.medicore.infrastructure.storage.contract.UrlGeneratorService;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class UrlGeneratorServiceImpl implements UrlGeneratorService {
    private final S3Properties s3Properties;
    private final S3Presigner s3Presigner;
    private final S3Utils s3Utils;

    @Override
    public URL generateViewUrl(UUID testId) {
        String key = s3Utils.buildKey(testId);
        s3Utils.checkObject(key);

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(request)
                .signatureDuration(Duration.ofMinutes(s3Properties.getUrlDurationMin()))
                .build();

        return s3Presigner.presignGetObject(presignRequest).url();
    }

    @Override
    public URL generateDownloadUrl(UUID testId) {
        String key = s3Utils.buildKey(testId);
        s3Utils.checkObject(key);

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(key)
                .responseContentDisposition(
                        "attachment; filename=\"blood_test_" + testId + ".pdf\""
                )
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(request)
                .signatureDuration(Duration.ofMinutes(s3Properties.getUrlDurationMin()))
                .build();

        return s3Presigner.presignGetObject(presignRequest).url();
    }
}
