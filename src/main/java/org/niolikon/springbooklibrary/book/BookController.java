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

import org.niolikon.springbooklibrary.book.web.BookRequest;
import org.niolikon.springbooklibrary.book.web.BookView;

@RestController
@RequestMapping("/books")
public class BookController {
    
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public BookView getBook(@PathVariable Long id) {
        return service.getBook(id);
    }

    @GetMapping
    @ResponseBody
    public Page<BookView> getAllBooks(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.findAllBooks(pageable);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public BookView create(@RequestBody @Valid BookRequest req) {
        return service.create(req);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        service.delete(id);
    }
    
    @PutMapping("/{id}")
    public BookView updateBook(@PathVariable Long id,
            @RequestBody @Valid BookRequest req) {
        Book book = service.findBookOrThrow(id);
        return service.update(book, req);
    }
}
