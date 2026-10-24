package de.thb.ccdo.storage;

import io.floci.testcontainers.FlociContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
class FileStorageIT {

    @Container
    static FlociContainer floci = new FlociContainer();

    @Test
    void uploadsAndDownloadsFile() {
        S3Client s3 = S3Client.builder()
                .endpointOverride(URI.create(floci.getEndpoint()))
                .region(Region.of(floci.getRegion()))
                .credentialsProvider(credentials(floci))
                .forcePathStyle(true)
                .build();

        var storage = new FileStorage(s3, "vorlesung");
        storage.createBucket();
        storage.upload("hallo.txt", "Hallo CCDO");

        assertThat(storage.download("hallo.txt")).isEqualTo("Hallo CCDO");
    }

    @Test
    void listsUploadedFiles() {
        var storage = new FileStorage(client(), "uebung");
        storage.createBucket();
        storage.upload("a.txt", "A");
        storage.upload("b.txt", "B");

        assertThat(storage.list()).containsExactlyInAnyOrder("a.txt", "b.txt");
    }

    @Test
    void failsForMissingFile() {
        var storage = new FileStorage(client(), "leer");
        storage.createBucket();

        assertThatThrownBy(() -> storage.download("gibt-es-nicht.txt"))
                .isInstanceOf(NoSuchKeyException.class);
    }

    // Zugangsdaten des Emulators – keine echten AWS-Credentials nötig
    private static StaticCredentialsProvider credentials(FlociContainer floci) {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(floci.getAccessKey(), floci.getSecretKey()));
    }

    private static S3Client client() {
        return S3Client.builder()
                .endpointOverride(URI.create(floci.getEndpoint()))
                .region(Region.of(floci.getRegion()))
                .credentialsProvider(credentials(floci))
                .forcePathStyle(true)
                .build();
    }
}
