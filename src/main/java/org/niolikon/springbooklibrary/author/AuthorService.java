package org.niolikon.springbooklibrary.author;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.niolikon.springbooklibrary.author.converter.AuthorToAuthorViewConverter;
import org.niolikon.springbooklibrary.author.web.AuthorRequest;
import org.niolikon.springbooklibrary.author.web.AuthorView;
import org.niolikon.springbooklibrary.system.MessageProvider;
import org.niolikon.springbooklibrary.system.exceptions.EntityDuplicationException;
import org.niolikon.springbooklibrary.system.exceptions.EntityNotFoundException;

@Service
public class AuthorService {
    
    private final AuthorRepository authorRepo;
    private final AuthorToAuthorViewConverter authorConverter;
    private final MessageProvider messageProvider;
    
    public AuthorService(AuthorRepository authorRepo,
            AuthorToAuthorViewConverter authorConverter,
            MessageProvider messageUtil) {
        this.authorRepo = authorRepo;
        this.authorConverter = authorConverter;
        this.messageProvider = messageUtil;
    }
    
    public Author findAuthorOrThrow(Long id) {
        return authorRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(messageProvider.getMessage("author.NotFound", id)));
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

        try {
            Author authorSaved = authorRepo.save(author);
            return authorConverter.convert(authorSaved);
        } catch (DataIntegrityViolationException e) {
            throw new EntityDuplicationException(messageProvider.getMessage("author.Duplication", author.getName(), author.getSurname()));
        }
    }
    
    @Transactional
    public void delete(Long id) {
        try {
            authorRepo.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(messageProvider.getMessage("author.NotFound", id));
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
