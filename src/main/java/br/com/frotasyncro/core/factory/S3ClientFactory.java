package br.com.frotasyncro.core.factory;

import br.com.frotasyncro.core.configuration.AppConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class S3ClientFactory {

    private final AppConfiguration appConfiguration;

    @Bean
    public StaticCredentialsProvider staticCredentialsProvider() {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                        appConfiguration.getAwsAccessKey(),
                        appConfiguration.getAwsSecretKey()
                )
        );
    }

    @Bean
    public S3Client s3Client(StaticCredentialsProvider staticCredentialsProvider) {
        return S3Client.builder()
                .region(Region.SA_EAST_1)
                .credentialsProvider(staticCredentialsProvider)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(StaticCredentialsProvider staticCredentialsProvider) {
        return S3Presigner.builder()
                .region(Region.SA_EAST_1)
                .credentialsProvider(staticCredentialsProvider)
                .build();
    }
}
