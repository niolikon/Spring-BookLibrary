package org.niolikon.springbooklibrary.book;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.niolikon.springbooklibrary.author.AuthorRepository;
import org.niolikon.springbooklibrary.book.web.BookRequest;
import org.niolikon.springbooklibrary.book.web.BookView;
import org.niolikon.springbooklibrary.book.converter.BookToBookViewConverter;
import org.niolikon.springbooklibrary.publisher.PublisherRepository;
import org.niolikon.springbooklibrary.system.exceptions.EntityNotFoundException;
import org.niolikon.springbooklibrary.commons.MessageUtil;

@Service
public class BookService {
    
    private final AuthorRepository authorRepo;
    private final BookRepository bookRepo;
    private final PublisherRepository publisherRepo;
    private final BookToBookViewConverter bookConverter;
    private final MessageUtil messageUtil;

    public BookService(AuthorRepository authorRepo,
            BookRepository bookRepo,
            PublisherRepository publisherRepo,
            BookToBookViewConverter bookConverter,
            MessageUtil messageUtil) {
        this.authorRepo = authorRepo;
        this.bookRepo = bookRepo;
        this.publisherRepo = publisherRepo;
        this.bookConverter = bookConverter;
        this.messageUtil = messageUtil;
    }
    
    public Book findBookOrThrow(Long id) {
        return bookRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(messageUtil.getMessage("book.NotFound", id)));
    }
    
    public BookView getBook(Long id) {
        Book book = findBookOrThrow(id);
        return bookConverter.convert(book);
    }

    public Page<BookView> findAllBooks(Pageable pageable) {
        Page<Book> books = bookRepo.findAll(pageable);
        List<BookView> bookViews = new ArrayList<>();
        books.forEach( book -> {
            BookView bookView = bookConverter.convert(book);
            bookViews.add(bookView);
        });
        return new PageImpl<>(bookViews, pageable, books.getTotalElements());
    }
    
    public BookView create(BookRequest req) {
        Book book = new Book();
        this.fetchFromRequest(book, req);
        Book bookSaved = bookRepo.save(book);
        return bookConverter.convert(bookSaved);
    }
    
    @Transactional
    public void delete(Long id) {
        try {
            bookRepo.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(messageUtil.getMessage("book.NotFound", id));
        }
    }
    
    public BookView update(Book book, BookRequest req) {
        Book bookUpdated = this.fetchFromRequest(book, req);
        Book bookSaved = bookRepo.save(bookUpdated);
        return bookConverter.convert(bookSaved);
    }
    
    private Book fetchFromRequest(Book book, BookRequest req) {
        book.setTitle(req.getTitle());
        book.setQuantity(req.getQuantity());
        book.setAuthor(authorRepo.getOne(req.getAuthorId()));
        book.setPublisher(publisherRepo.getOne(req.getPublisherId()));
        return book;
    }
}
