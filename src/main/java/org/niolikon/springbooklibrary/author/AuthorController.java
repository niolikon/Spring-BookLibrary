package org.niolikon.springbooklibrary.author;

import javax.validation.Valid;

import org.niolikon.springbooklibrary.author.web.AuthorRequest;
import org.niolikon.springbooklibrary.author.web.AuthorView;
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

import org.niolikon.springbooklibrary.author.Author;
import org.niolikon.springbooklibrary.author.AuthorService;

@RestController
@RequestMapping("/authors")
public class AuthorController {
    
    private final AuthorService service;
    
    public AuthorController(AuthorService service) {
        this.service = service;
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public AuthorView getAuthor(@PathVariable Long id) {
        return service.getAuthor(id);
    }

    @GetMapping
    @ResponseBody
    public Page<AuthorView> getAllAuthors(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.findAllAuthors(pageable);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public AuthorView create(@RequestBody @Valid AuthorRequest req) {
        return service.create(req);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable Long id) {
        service.delete(id);
    }
    
    @PutMapping("/{id}")
    public AuthorView updateAuthor(@PathVariable Long id,
            @RequestBody @Valid AuthorRequest req) {
        Author author = service.findAuthorOrThrow(id);
        return service.update(author, req);
    }
}
