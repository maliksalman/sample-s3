package com.smalik.s3sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SampleService {

    private Lorem lorem;
    private ObjectMapper mapper;
    private S3Client client;
    private String bucket;

    public SampleService(S3Client client, @Value("${s3.bucket}") String bucket) {
        this.client = client;
        this.bucket = bucket;
        this.mapper = new ObjectMapper();
        this.lorem = LoremIpsum.getInstance();
    }

    public Sample create() throws JsonProcessingException {

        Sample sample = new Sample();
        sample.setCreated(new Date());
        sample.setId(UUID.randomUUID().toString());
        sample.setData(lorem.getWords(5,7));

        client.putObject(
            PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(sample.getId())
                    .contentType("application/json")
                    .build(),
            RequestBody.fromString(mapper.writeValueAsString(sample)));
        return sample;
    }

    public List<String> listSortedByLastModified() {
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
