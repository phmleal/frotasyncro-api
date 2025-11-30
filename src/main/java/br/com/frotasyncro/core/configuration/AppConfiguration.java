package br.com.frotasyncro.core.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AppConfiguration {

    @Value("${API.SECRET.KEY}")
    private String secretKey;

    @Value("${API.BUCKET.NAME}")
    private String apiBucketName;

    @Value("${API.MAIL.FROM}")
    private String apiMailFrom;

    @Value("${API.PASSWORD.LENGTH:8}")
    private int apiPasswordLength;

    @Value("${OTP.MAX.ATTEMPTS:5}")
    private int otpMaxAttempts;

    @Value("${AWS.ACCESS.KEY}")
    private String awsAccessKey;

    @Value("${AWS.SECRET.ACCESS}")
    private String awsSecretKey;

}
