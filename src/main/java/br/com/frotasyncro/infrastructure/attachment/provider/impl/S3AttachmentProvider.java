package br.com.frotasyncro.infrastructure.attachment.provider.impl;

import br.com.frotasyncro.domain.report.exception.ReportGenerationException;
import br.com.frotasyncro.infrastructure.attachment.provider.AttachmentProvider;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.text.Normalizer;
import java.time.Duration;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public record S3AttachmentProvider(S3Client s3Client,
                                   S3Presigner s3Presigner) implements AttachmentProvider<String> {

    private static final String PROVIDER_TYPE = "S3";
    private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofHours(1);

    private static String sanitizeKey(String key, Pattern allowed) {
        if (key == null) return "";
        String lower = key.trim().toLowerCase().replaceAll("\\s+", "-");
        // Remove characters not allowed and keep a safe subset
        StringBuilder sb = new StringBuilder();
        for (char c : lower.toCharArray()) {
            if (allowed.matcher(String.valueOf(c)).matches()) {
                sb.append(c);
            } else {
                // replace others with '-'
                sb.append('-');
            }
        }
        return sb.toString();
    }

    private static String sanitizeValue(String value) {
        if (value == null) return "";
        // Trim and remove problematic control characters
        String trimmed = value.trim().replaceAll("[\\r\\n\\t]+", " ");
        // Normalize and remove diacritics (e.g. 'ó' -> 'o') to avoid non-ASCII bytes in metadata
        String normalized = Normalizer.normalize(trimmed, Normalizer.Form.NFKD);
        String withoutDiacritics = normalized.replaceAll("\\p{M}", "");
        // Remove any remaining non-ASCII characters
        String asciiOnly = withoutDiacritics.replaceAll("[^\u0000-\u007F]", "");
        // Truncate to a safe length (256) to avoid very large metadata
        if (asciiOnly.length() > 256) {
            return asciiOnly.substring(0, 256);
        }
        return asciiOnly;
    }

    @Override
    public String upload(byte[] content, String bucket, String key, String contentType) {
        log.info("Iniciando upload para S3: bucket='{}', key='{}'", bucket, key);

        try {
            // Upload do arquivo para S3
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(content));
            log.info("Arquivo enviado com sucesso para S3: s3://{}/{}", bucket, key);

            return generatePresignedUrl(bucket, key);

        } catch (Exception e) {
            log.error("Erro ao fazer upload para S3: {}", e.getMessage(), e);
            throw new ReportGenerationException("Erro ao fazer upload do relatório no S3: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadWithMetadata(byte[] content, String bucket, String key, String contentType, Map<String, String> metadata) {

        try {
            // Sanitize metadata keys/values to avoid characters that may break signing/canonicalization
            Pattern allowedKeyPattern = Pattern.compile("[a-z0-9-_.]+");
            // Handle null metadata and avoid empty/duplicate keys after sanitization
            java.util.Map<String, String> temp = (metadata == null) ? java.util.Collections.emptyMap() : metadata.entrySet().stream()
                    .filter(e -> e.getKey() != null && e.getValue() != null)
                    .collect(Collectors.toMap(
                            e -> sanitizeKey(e.getKey(), allowedKeyPattern),
                            e -> sanitizeValue(e.getValue()),
                            (v1, v2) -> v1 // keep first value if sanitized keys collide
                    ));

            java.util.Map<String, String> sanitizedMetadata = temp.entrySet().stream()
                    .filter(e -> e.getKey() != null && !e.getKey().isBlank())
                    .collect(Collectors.toMap(java.util.Map.Entry::getKey, java.util.Map.Entry::getValue));

            // Upload do arquivo com metadados
            PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType);

            // Adicionar metadados customizados
            if (!sanitizedMetadata.isEmpty()) {
                requestBuilder.metadata(sanitizedMetadata);
            }

            PutObjectRequest putRequest = requestBuilder.build();
            s3Client.putObject(putRequest, RequestBody.fromBytes(content));
            log.info("Arquivo com metadados enviado com sucesso para S3: s3://{}/{}", bucket, key);

            return generatePresignedUrl(bucket, key);

        } catch (Exception e) {
            log.error("Erro ao fazer upload com metadados para S3: {}", e.getMessage(), e);
            throw new ReportGenerationException("Erro ao fazer upload do relatório no S3: " + e.getMessage(), e);
        }
    }

    @Override
    public String getPresignedUrl(String bucket, String key) {
        return generatePresignedUrl(bucket, key);
    }

    @Override
    public String getProviderType() {
        return PROVIDER_TYPE;
    }

    @Override
    public boolean validateConfiguration() {
        log.debug("Validando configuração do S3");
        try {
            if (s3Client == null || s3Presigner == null) {
                log.error("S3Client ou S3Presigner não foi injetado");
                return false;
            }
            log.info("Configuração do S3 validada com sucesso");
            return true;
        } catch (Exception e) {
            log.error("Erro ao validar configuração do S3: {}", e.getMessage(), e);
            return false;
        }
    }

    private String generatePresignedUrl(String bucket, String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(
                    builder -> builder
                            .signatureDuration(PRESIGNED_URL_EXPIRATION)
                            .getObjectRequest(getObjectRequest)
            );

            String presignedUrl = presignedGetObjectRequest.url().toString();
            log.info("Presigned URL gerada com sucesso para s3://{}/{} com expiração de {} horas",
                    bucket, key, PRESIGNED_URL_EXPIRATION.toHours());
            return presignedUrl;

        } catch (Exception e) {
            log.error("Erro ao gerar presigned URL para s3://{}/{}: {}", bucket, key, e.getMessage(), e);
            throw new ReportGenerationException("Erro ao gerar URL assinada para download: " + e.getMessage(), e);
        }
    }
}
