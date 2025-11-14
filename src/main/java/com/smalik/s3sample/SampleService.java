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

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
        client.putObject(b -> b
                        .bucket(config.getBucket())
                        .key(sample.getId())
                        .contentType("application/json")
                        .acl(ObjectCannedACL.AUTHENTICATED_READ),
                RequestBody.fromString(mapper.writeValueAsString(sample)));
        return sample;
    }

    public List<String> listSortedByLastModified() {
        ListObjectsV2Response response = client.listObjectsV2(b -> b.bucket(config.getBucket()));
        return response.contents().stream()
                .sorted(Comparator.comparing(S3Object::lastModified))
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    public Sample findById(String id) throws JsonProcessingException {
        ResponseBytes<GetObjectResponse> bytes = client.getObjectAsBytes(b -> b
                .bucket(config.getBucket())
                .key(id));
        return mapper.readValue(bytes.asUtf8String(), Sample.class);
    }

    public Map<String, Object> findMetadataById(String id) {
        HeadObjectResponse response = client.headObject(b -> b
                .bucket(config.getBucket())
                .key(id));
        return Map.of(
                "content-length", response.contentLength(),
                "etag", response.eTag(),
                "last-modified", response.lastModified());
    }

    public URL getPresignedUrl(String id, int timeToLiveMinutes) {
        return presigner
                .presignGetObject(b -> b
                        .getObjectRequest(orb -> orb
                                .bucket(config.getBucket())
                                .key(id))
                        .signatureDuration(Duration.ofMinutes(timeToLiveMinutes)))
                .url();
    }

    public void deleteById(String id) {
        client.deleteObject(b -> b
                .bucket(config.getBucket())
                .key(id));
    }
}
