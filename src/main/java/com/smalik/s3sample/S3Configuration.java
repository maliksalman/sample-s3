package com.smalik.s3sample;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class S3Configuration {

    @Bean
    public S3Client createClient(S3ClientConfiguration configuration) {

        // the credentials and region are looked up a different way on an actual EC2 instance
        AwsBasicCredentials credentials = AwsBasicCredentials.create(configuration.getAccessId(), configuration.getSecretKey());
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
        S3ClientBuilder s3ClientBuilder = S3Client.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(configuration.getRegion()));

        try {
            if (configuration.getEndpoint() != null) {
                s3ClientBuilder = s3ClientBuilder.endpointOverride(new URI(configuration.getEndpoint()));
            }
            return s3ClientBuilder.build();

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Component
    @ConfigurationProperties(prefix = "s3")
    public static class S3ClientConfiguration {

        private String bucket;
        private String endpoint;
        private String accessId;
        private String secretKey;
        private String region;

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessId() {
            return accessId;
        }

        public void setAccessId(String accessId) {
            this.accessId = accessId;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }
    }
}
