package com.smalik.s3sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

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

    @Bean
    @SneakyThrows
    public S3Presigner s3Presigner(S3ClientConfig config) {
        return S3Presigner.builder()
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        config.getAccessId(),
                                        config.getSecretKey())))
                .region(Region.of(config.getRegion()))
                .endpointOverride(config.getEndpoint())
                .build();
    }

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.build();
    }

    @Bean
    public Lorem lorem() {
        return LoremIpsum.getInstance();
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
