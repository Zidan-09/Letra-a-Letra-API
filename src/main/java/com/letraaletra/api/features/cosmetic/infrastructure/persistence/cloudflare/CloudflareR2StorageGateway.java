package com.letraaletra.api.features.cosmetic.infrastructure.persistence.cloudflare;

import com.letraaletra.api.features.cosmetic.application.port.AssetStorageGateway;
import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
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
            logger.error("Error on upload asset:", e);

            throw e;
        }
    }
}
