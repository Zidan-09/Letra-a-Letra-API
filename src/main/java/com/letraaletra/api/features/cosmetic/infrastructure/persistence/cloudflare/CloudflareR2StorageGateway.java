package com.letraaletra.api.features.cosmetic.infrastructure.persistence.cloudflare;

import com.letraaletra.api.features.cosmetic.application.port.AssetStorageGateway;
import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class CloudflareR2StorageGateway implements AssetStorageGateway {
    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    private final Logger logger = LoggerFactory.getLogger(CloudflareR2StorageGateway.class);

    public CloudflareR2StorageGateway(S3Client client) {
        this.s3Client = client;
    }

    @Override
    public String upload(
            byte[] file,
            String fileName,
            CosmeticTypes cosmeticType
    ) {
        try {
            String nameSaved = cosmeticType.name() + "/" + fileName + ".webp";

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(nameSaved)
                            .contentType("image/webp")
                            .build(),
                    RequestBody.fromBytes(file)
            );

            return nameSaved;

        } catch (Exception e) {
            logger.error("Error on upload asset to CDN");

            throw e;
        }
    }

    @Override
    public String copy(String oldPath, String newName, CosmeticTypes newType) {
        try {
            String nameSaved = newType + "/" + newName + ".webp";

            s3Client.copyObject(
                    CopyObjectRequest.builder()
                            .sourceBucket(bucket)
                            .sourceKey(oldPath)
                            .destinationBucket(bucket)
                            .destinationKey(nameSaved)
                            .build()
            );

            return nameSaved;

        } catch (Exception e) {
            logger.error("Error to move asset on CDN");

            throw e;
        }
    }

    @Override
    public void delete(String assetPath) {
        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(assetPath)
                            .build()
            );
        } catch (Exception e) {
            logger.error("Error on delete asset from CDN");

            throw e;
        }
    }
}
