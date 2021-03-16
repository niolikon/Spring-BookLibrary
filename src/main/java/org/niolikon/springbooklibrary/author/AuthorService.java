package org.niolikon.springbooklibrary.author;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.niolikon.springbooklibrary.author.converter.AuthorToAuthorViewConverter;
import org.niolikon.springbooklibrary.author.web.AuthorRequest;
import org.niolikon.springbooklibrary.author.web.AuthorView;
import org.niolikon.springbooklibrary.system.exceptions.EntityNotFoundException;
import org.niolikon.springbooklibrary.commons.MessageUtil;

@Service
public class AuthorService {
    
    private final AuthorRepository authorRepo;
    private final AuthorToAuthorViewConverter authorConverter;
    private final MessageUtil messageUtil;
    
    public AuthorService(AuthorRepository authorRepo,
            AuthorToAuthorViewConverter authorConverter,
            MessageUtil messageUtil) {
        this.authorRepo = authorRepo;
        this.authorConverter = authorConverter;
        this.messageUtil = messageUtil;
    }
    
    public Author findAuthorOrThrow(Long id) {
        return authorRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(messageUtil.getMessage("author.NotFound", id)));
    }
    
    public AuthorView getAuthor(Long id) {
        Author author = findAuthorOrThrow(id);
        return authorConverter.convert(author);
    }

    public Page<AuthorView> findAllAuthors(Pageable pageable) {
        Page<Author> authors = authorRepo.findAll(pageable);
        List<AuthorView> authorViews = new ArrayList<>();
        authors.forEach(author -> {
            AuthorView authorView = authorConverter.convert(author);
            authorViews.add(authorView);
        });
        return new PageImpl<>(authorViews, pageable, authors.getTotalElements());
    }
    
    public AuthorView create(AuthorRequest req) {
        Author author = new Author();
        this.fetchFromRequest(author, req);
        Author authorSaved = authorRepo.save(author);
        return authorConverter.convert(authorSaved);
    }
    
    @Transactional
    public void delete(Long id) {
        try {
            authorRepo.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(messageUtil.getMessage("author.NotFound", id));
        }
    }
    
    public AuthorView update(Author author, AuthorRequest req) {
        Author authorUpdated = this.fetchFromRequest(author, req);
        Author authorSaved = authorRepo.save(authorUpdated);
        return authorConverter.convert(authorSaved);
    }
    
    private Author fetchFromRequest(Author author, AuthorRequest req) {
        author.setName(req.getName());
        author.setSurname(req.getSurname());
        return author;
    }
}
