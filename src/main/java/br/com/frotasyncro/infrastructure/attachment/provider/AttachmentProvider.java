package br.com.frotasyncro.infrastructure.attachment.provider;

public interface AttachmentProvider<T> {

    T upload(byte[] content, String bucket, String key, String contentType);

    T uploadWithMetadata(byte[] content, String bucket, String key, String contentType, java.util.Map<String, String> metadata);

    String getPresignedUrl(String bucket, String key);

    String getProviderType();

    boolean validateConfiguration();
}
