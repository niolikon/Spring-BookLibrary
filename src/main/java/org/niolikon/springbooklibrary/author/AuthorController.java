package org.niolikon.springbooklibrary.author;

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

import org.niolikon.springbooklibrary.author.web.AuthorRequest;
import org.niolikon.springbooklibrary.author.web.AuthorView;

@RestController
@RequestMapping("/authors")
@Api(tags="Management of Author entities")
public class AuthorController {
    
    private final AuthorService service;
    
    public AuthorController(AuthorService service) {
        this.service = service;
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    @ApiOperation(
            value = "Read author by ID", notes = "Returns Author data in JSON", response = AuthorView.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The Author has been fetched"),
            @ApiResponse(code = 404, message = "Could not find the specified Author"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public AuthorView getAuthor(@ApiParam("The ID of the Author") @PathVariable Long id) {
        return service.getAuthor(id);
    }

    @GetMapping
    @ResponseBody
    @ApiOperation(
            value = "Read all authors", notes = "Returns Author data in JSON", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The Authors have been fetched"),
            @ApiResponse(code = 404, message = "No Authors are present in the repository"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public Page<AuthorView> getAllAuthors(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.findAllAuthors(pageable);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @ApiOperation(
            value = "Create an author", notes = "Stores the input JSON Author data", response = AuthorView.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The Author has been stored"),
            @ApiResponse(code = 409, message = "Could not complete the storage, the input Author data would cause duplication"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public AuthorView create(@ApiParam("The input Author data") @RequestBody @Valid AuthorRequest req) {
        return service.create(req);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(
            value = "Delete an author", notes = "Deletes the specified Author data", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "The Author has been deleted"),
            @ApiResponse(code = 404, message = "Could not find the specified Author"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public void deleteAuthor(@ApiParam("The ID of the Author") @PathVariable Long id) {
        service.delete(id);
    }
    
    @PutMapping("/{id}")
    @ApiOperation(
            value = "Update an author", notes = "Modifies the specified Author with the input JSON Author data", response = AuthorView.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The Author has been modified"),
            @ApiResponse(code = 404, message = "Could not find the specified Author"),
            @ApiResponse(code = 409, message = "Could not complete the modification, the input Author data would cause duplication"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public AuthorView updateAuthor(@ApiParam("The ID of the Author") @PathVariable Long id,
            @ApiParam("The input Author data") @RequestBody @Valid AuthorRequest req) {
        Author author = service.findAuthorOrThrow(id);
        return service.update(author, req);
    }
}
