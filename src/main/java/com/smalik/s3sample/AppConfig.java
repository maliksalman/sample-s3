package com.smalik.s3sample;

import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class AppConfig {

    @Bean
    @SneakyThrows
    public S3Client s3Client(S3ClientConfig config) {
        return S3Client.builder()
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        config.getAccessId(),
                                        config.getSecretKey())))
                .region(Region.of(config.getRegion()))
                .endpointOverride(config.getEndpoint())
                .build();
    }

    @Component
    @Data
    @ConfigurationProperties(prefix = "s3")
    public static class S3ClientConfig {
        private String bucket;
        private URI endpoint;
        private String accessId;
        private String secretKey;
        private String region;
    }
}
