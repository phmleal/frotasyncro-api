package br.com.frotasyncro.infrastructure.attachment.provider.factory;

import br.com.frotasyncro.infrastructure.attachment.provider.AttachmentProvider;
import br.com.frotasyncro.infrastructure.attachment.provider.impl.S3AttachmentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * Factory de configuração para provedores de anexos.
 * Define os beans dos provedores que serão injetados na aplicação.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AttachmentProviderFactory {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Bean
    public AttachmentProvider<String> attachmentProvider() {
        log.info("Inicializando S3AttachmentProvider");
        S3AttachmentProvider provider = new S3AttachmentProvider(s3Client, s3Presigner);

        if (!provider.validateConfiguration()) {
            log.warn("Configuração do S3AttachmentProvider pode estar incompleta");
        }

        return provider;
    }
}

