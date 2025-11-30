package br.com.frotasyncro.domain.attachment.service;

import br.com.frotasyncro.infrastructure.attachment.provider.AttachmentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentS3Service {

    private final AttachmentProvider<String> attachmentProvider;

    @Value("${aws.s3.bucket:driveco-attachments}")
    private String s3Bucket;

    public String generatePresignedUrl(String s3Key) {
        try {
            log.debug("Gerando presigned URL para S3 key: {}", s3Key);
            return attachmentProvider.getPresignedUrl(s3Bucket, s3Key);
        } catch (Exception e) {
            log.error("Erro ao gerar presigned URL para S3 key: {}", s3Key, e);
            throw new RuntimeException("Erro ao gerar presigned URL: " + e.getMessage(), e);
        }
    }

    /**
     * Faz upload de arquivo para S3 e retorna a presigned URL para download.
     *
     * @param fileContent conteúdo do arquivo
     * @param bucket      nome do bucket S3
     * @param key         chave (caminho) do arquivo no S3
     * @param contentType tipo de conteúdo do arquivo
     * @param metadata    metadados adicionais
     * @return presigned URL para download do arquivo
     */
    public String uploadFileAndGetPresignedUrl(byte[] fileContent, String bucket, String key, String contentType, java.util.Map<String, String> metadata) {
        try {
            log.info("Iniciando upload para S3: bucket='{}', key='{}'", bucket, key);
            return attachmentProvider.uploadWithMetadata(fileContent, bucket, key, contentType, metadata);
        } catch (Exception e) {
            log.error("Erro ao fazer upload para S3: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao fazer upload para S3: " + e.getMessage(), e);
        }
    }

}
