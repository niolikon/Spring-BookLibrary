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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.niolikon.springbooklibrary.publisher.web.PublisherRequest;
import org.niolikon.springbooklibrary.publisher.web.PublisherView;

@RestController
@RequestMapping("/publishers")
@Api(tags="Management of Publisher entities")
public class PublisherController {
    
    private final PublisherService service;

    public PublisherController(PublisherService service) {
        this.service = service;
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    @ApiOperation(
            value = "Read publisher by ID", notes = "Returns Publisher data in JSON", response = PublisherView.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The Publisher has been fetched"),
            @ApiResponse(code = 404, message = "Could not find the specified Publisher"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public PublisherView getPublisher(@ApiParam("The ID of the Publisher") @PathVariable Long id) {
        return service.getPublisher(id);
    }

    @GetMapping
    @ResponseBody
    @ApiOperation(
            value = "Read all publishers", notes = "Returns Publisher data in JSON", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The Publishers have been fetched"),
            @ApiResponse(code = 404, message = "No Publishers are present in the repository"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public Page<PublisherView> getAllPublishers(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.findAllPublishers(pageable);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @ApiOperation(
            value = "Create a publisher", notes = "Stores the input JSON Publisher data", response = PublisherView.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The Publisher has been stored"),
            @ApiResponse(code = 409, message = "Could not complete the storage, the input Publisher data would cause duplication"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public PublisherView create(@ApiParam("The input Publisher data") @RequestBody @Valid PublisherRequest req) {
        return service.create(req);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(
            value = "Delete a publisher", notes = "Deletes the specified Publisher data", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "The Publisher has been deleted"),
            @ApiResponse(code = 404, message = "Could not find the specified Publisher"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public void deletePublisher(@ApiParam("The ID of the Publisher") @PathVariable Long id) {
        service.delete(id);
    }
    
    @PutMapping("/{id}")
    @ApiOperation(
            value = "Update a publisher", notes = "Modifies the specified Publisher with the input JSON Publisher data", response = PublisherView.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The Publisher has been modified"),
            @ApiResponse(code = 404, message = "Could not find the specified Publisher"),
            @ApiResponse(code = 409, message = "Could not complete the modification, the input Publisher data would cause duplication"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public PublisherView updatePublisher(@ApiParam("The ID of the Publisher") @PathVariable Long id,
            @ApiParam("The input Publisher data") @RequestBody @Valid PublisherRequest req) {
        Publisher publisher = service.findPublisherOrThrow(id);
        return service.update(publisher, req);
    }
    
}
