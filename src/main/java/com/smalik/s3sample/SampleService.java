package com.smalik.s3sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SampleService {

    private ObjectMapper mapper;
    private S3Client client;
    private String bucket;

    public SampleService(S3Client client, @Value("${bucket:sample}") String bucket) {
        this.client = client;
        this.bucket = bucket;
        this.mapper = new ObjectMapper();
    }

    public Sample save(Sample sample) throws JsonProcessingException {
        client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(sample.getId())
                        .contentType("application/json")
                        .build(),
                RequestBody.fromString(mapper.writeValueAsString(sample)));
        return sample;
    }

    public List<String> listSortByCreationTime() {
        ListObjectsV2Response response = client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucket)
                .build());
        return response.contents().stream()
                .sorted((o1,o2) -> o1.lastModified().compareTo(o2.lastModified()))
                .map(o -> o.key())
                .collect(Collectors.toList());
    }

    public Optional<Sample> findById(String id) throws JsonProcessingException {
        ResponseBytes<GetObjectResponse> bytes = client.getObjectAsBytes(GetObjectRequest.builder()
                .bucket(bucket)
                .key(id)
                .build());

        return Optional.of(mapper.readValue(bytes.asUtf8String(), Sample.class));
    }

    public void deleteById(String id) {
        client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(id)
                .build());
    }
}
