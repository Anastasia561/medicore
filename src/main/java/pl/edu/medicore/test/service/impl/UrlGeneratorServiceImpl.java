package pl.edu.medicore.test.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.medicore.config.properties.S3Properties;
import pl.edu.medicore.test.service.contract.UrlGeneratorService;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.time.Duration;

@Service
@RequiredArgsConstructor
class UrlGeneratorServiceImpl implements UrlGeneratorService {
    private final S3Properties s3Properties;
    private final S3Presigner s3Presigner;

    @Override
    public URL generateViewUrl(Long testId) {
        String key = buildKey(testId);

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
    public URL generateDownloadUrl(Long testId) {

        String key = buildKey(testId);

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

    private String buildKey(Long testId) {
        return s3Properties.getFolderName() + "/" +
                testId + "/" +
                s3Properties.getFileName();
    }
}
