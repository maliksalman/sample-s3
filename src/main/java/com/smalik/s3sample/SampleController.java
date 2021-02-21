package com.smalik.s3sample;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    public Optional<Sample> getSample(@PathVariable("id") String id) throws Exception {
        return service.findById(id);
    }

    @DeleteMapping("/s3/{id}")
    public void deleteSample(@PathVariable("id") String id) {
        service.deleteById(id);
    }
}
