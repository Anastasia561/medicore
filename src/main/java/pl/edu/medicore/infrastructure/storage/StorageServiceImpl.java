package pl.edu.medicore.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.medicore.config.properties.S3Properties;
import pl.edu.medicore.exception.FileNotFoundException;
import pl.edu.medicore.exception.UploadFileException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
class StorageServiceImpl implements StorageService {

    private final S3Properties s3Properties;
    private final S3Client s3Client;

    @Override
    public void uploadTest(MultipartFile file, Long testId) {
        String key = buildKey(testId);

        try (InputStream inputStream = file.getInputStream()) {

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(key)
                    .contentType("application/pdf")
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(inputStream, file.getSize())
            );

        } catch (IOException e) {
            throw new UploadFileException("Failed to upload blood test file");
        }
    }

    @Override
    public void deleteTest(Long testId) {
        String key = buildKey(testId);

        if (!objectExists(key)) {
            throw new FileNotFoundException("File not found");
        }

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    public InputStream getFile(Long testId) {
        String key = buildKey(testId);
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(key)
                .build();

        return s3Client.getObject(request);
    }

    private boolean objectExists(String key) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(key)
                    .build();

            s3Client.headObject(request);
            return true;

        } catch (S3Exception e) {
            return false;
        }
    }

    private String buildKey(Long testId) {
        return s3Properties.getFolderName() + "/" +
                testId + "/" +
                s3Properties.getFileName();
    }
}
