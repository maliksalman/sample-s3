package com.smalik.s3sample;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.net.URL;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SampleController {

    private final SampleService service;

    @PostMapping("/s3")
    public Sample createSample() throws Exception {
        return service.create();
    }

    @GetMapping("/s3")
    public List<String> listSamples() {
        return service.listSortedByLastModified();
    }

    @GetMapping("/s3/{id}")
    public Sample getSample(@PathVariable("id") String id) throws Exception {
        return service.findById(id);
    }

    @GetMapping("/s3/{id}/url")
    public URL getPresignedUrl(
            @PathVariable("id") String id,
            @RequestParam(name="timeToLiveMinutes", defaultValue="5", required=false) int timeToLiveMinutes) {
        return service.getPresignedUrl(id, timeToLiveMinutes);
    }

    @DeleteMapping("/s3/{id}")
    public void deleteSample(@PathVariable("id") String id) {
        service.deleteById(id);
    }


    @ControllerAdvice
    static class ErrorHandling {
        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler(NoSuchKeyException.class)
        public void handleNoSuchKeyException() { /* nothing to do */ }
    }
}
