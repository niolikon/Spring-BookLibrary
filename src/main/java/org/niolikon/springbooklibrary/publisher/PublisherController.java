package org.niolikon.springbooklibrary.publisher;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.niolikon.springbooklibrary.publisher.web.PublisherRequest;
import org.niolikon.springbooklibrary.publisher.web.PublisherView;

@RestController
@RequestMapping("/publishers")
public class PublisherController {
    
    private final PublisherService service;

    public PublisherController(PublisherService service) {
        this.service = service;
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public PublisherView getPublisher(@PathVariable Long id) {
        return service.getPublisher(id);
    }

    @GetMapping
    @ResponseBody
    public Page<PublisherView> getAllPublishers(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.findAllPublishers(pageable);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PublisherView create(@RequestBody @Valid PublisherRequest req) {
        return service.create(req);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePublisher(@PathVariable Long id) {
        service.delete(id);
    }
    
    @PutMapping("/{id}")
    public PublisherView updatePublisher(@PathVariable Long id,
            @RequestBody @Valid PublisherRequest req) {
        Publisher publisher = service.findPublisherOrThrow(id);
        return service.update(publisher, req);
    }
    
}
