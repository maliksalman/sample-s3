package com.smalik.s3sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedeanda.lorem.Lorem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SampleService {

    private final Lorem lorem;
    private final ObjectMapper mapper;
    private final S3Client client;
    private final S3Presigner presigner;
    private final AppConfig.S3ClientConfig config;

    public Sample create() throws JsonProcessingException {
        Sample sample = Sample.builder()
                .created(LocalDateTime.now())
                .id(UUID.randomUUID().toString())
                .data(lorem.getWords(5, 7))
                .build();
        client.putObject(
                PutObjectRequest.builder()
                        .bucket(config.getBucket())
                        .key(sample.getId())
                        .contentType("application/json")
                        .acl(ObjectCannedACL.BUCKET_OWNER_FULL_CONTROL)
                        .build(),
                RequestBody.fromString(mapper.writeValueAsString(sample)));
        return sample;
    }

    public List<String> listSortedByLastModified() {
        ListObjectsV2Response response = client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(config.getBucket())
                .build());
        return response.contents().stream()
                .sorted(Comparator.comparing(S3Object::lastModified))
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    public Sample findById(String id) throws JsonProcessingException {
        ResponseBytes<GetObjectResponse> bytes = client.getObjectAsBytes(GetObjectRequest.builder()
                .bucket(config.getBucket())
                .key(id)
                .build());
        return mapper.readValue(bytes.asUtf8String(), Sample.class);
    }

    public URL getPresignedUrl(String id, int timeToLiveMinutes) {
        return presigner
                .presignGetObject(GetObjectPresignRequest.builder()
                        .getObjectRequest(GetObjectRequest.builder()
                                .bucket(config.getBucket())
                                .key(id)
                                .build())
                        .signatureDuration(Duration.ofMinutes(timeToLiveMinutes))
                        .build())
                .url();
    }

    public void deleteById(String id) {
        client.deleteObject(DeleteObjectRequest.builder()
                .bucket(config.getBucket())
                .key(id)
                .build());
    }
}
