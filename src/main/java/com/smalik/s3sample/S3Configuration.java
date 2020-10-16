package com.smalik.s3sample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class S3Configuration {

    @Bean
    public S3Client client(

    ) {
        try {
            return S3Client.builder()
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("minio", "changeme")))
                    .endpointOverride(new URI("http://localhost:9001"))
                    .region(Region.US_EAST_1)
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
