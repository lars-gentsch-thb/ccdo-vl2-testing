package de.thb.ccdo.storage;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

/**
 * Speichert Textdateien in einem S3-Bucket.
 * Der S3Client wird von außen übergeben – in Produktion zeigt er auf AWS,
 * im Test auf den Emulator im Container.
 */
public class FileStorage {

    private final S3Client s3;
    private final String bucket;

    public FileStorage(S3Client s3, String bucket) {
        this.s3 = s3;
        this.bucket = bucket;
    }

    public void createBucket() {
        s3.createBucket(b -> b.bucket(bucket));
    }

    public void upload(String key, String content) {
        s3.putObject(b -> b.bucket(bucket).key(key), RequestBody.fromString(content));
    }

    public String download(String key) {
        return s3.getObjectAsBytes(b -> b.bucket(bucket).key(key)).asUtf8String();
    }

    public List<String> list() {
        return s3.listObjectsV2(b -> b.bucket(bucket)).contents().stream()
                .map(S3Object::key)
                .toList();
    }
}
