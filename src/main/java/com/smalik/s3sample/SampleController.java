package com.smalik.s3sample;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class SampleController {

    private SampleService service;
    private Lorem lorem = LoremIpsum.getInstance();

    public SampleController(SampleService service) {
        this.service = service;
    }

    @PostMapping("/s3")
    public Sample createSample() throws Exception {
        Sample sample = new Sample();
        sample.setCreated(new Date());
        sample.setId(UUID.randomUUID().toString());
        sample.setData(lorem.getParagraphs(1,1));
        return service.save(sample);
    }

    @GetMapping("/s3")
    public List<String> listSamples() {
        return service.listSortByCreationTime();
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
