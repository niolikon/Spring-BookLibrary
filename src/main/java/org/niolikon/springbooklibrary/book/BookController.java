package org.niolikon.springbooklibrary.book;

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
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import org.niolikon.springbooklibrary.book.web.BookRequest;
import org.niolikon.springbooklibrary.book.web.BookView;

@RestController
@RequestMapping("/books")
@Api(tags="Management of Book entities")
public class BookController {
    
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    @ApiOperation(
            value = "Read book by ID", notes = "Returns Book data in JSON", response = BookView.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The Book has been fetched"),
            @ApiResponse(code = 404, message = "Could not find the specified Book"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public BookView getBook(@ApiParam("The ID of the Book") @PathVariable Long id) {
        return service.getBook(id);
    }

    @GetMapping
    @ResponseBody
    @ApiOperation(
            value = "Read all books", notes = "Returns Book data in JSON", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The Books have been fetched"),
            @ApiResponse(code = 404, message = "No Books are present in the repository"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public Page<BookView> getAllBooks(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.findAllBooks(pageable);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @ApiOperation(
            value = "Create a book", notes = "Stores the input JSON Book data", response = BookView.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The Book has been stored"),
            @ApiResponse(code = 409, message = "Could not complete the storage, the input Book data would cause duplication"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public BookView create(@ApiParam("The input Book data") @RequestBody @Valid BookRequest req) {
        return service.create(req);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(
            value = "Delete a book", notes = "Deletes the specified Book data", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "The Book has been deleted"),
            @ApiResponse(code = 404, message = "Could not find the specified Book"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public void deleteBook(@ApiParam("The ID of the Book") @PathVariable Long id) {
        service.delete(id);
    }
    
    @PutMapping("/{id}")
    @ApiOperation(
            value = "Update a book", notes = "Modifies the specified Book with the input JSON Book data", response = BookView.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The Book has been modified"),
            @ApiResponse(code = 404, message = "Could not find the specified Book"),
            @ApiResponse(code = 409, message = "Could not complete the modification, the input Book data would cause duplication"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public BookView updateBook(@ApiParam("The ID of the Book") @PathVariable Long id,
            @ApiParam("The input Book data") @RequestBody @Valid BookRequest req) {
        Book book = service.findBookOrThrow(id);
        return service.update(book, req);
    }
}
