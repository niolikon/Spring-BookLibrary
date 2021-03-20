package org.niolikon.springbooklibrary.book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.niolikon.springbooklibrary.author.Author;
import org.niolikon.springbooklibrary.book.converter.BookToBookViewConverter;
import org.niolikon.springbooklibrary.book.web.BookRequest;
import org.niolikon.springbooklibrary.book.web.BookView;
import org.niolikon.springbooklibrary.commons.DBUnitTest;
import org.niolikon.springbooklibrary.publisher.Publisher;
import org.niolikon.springbooklibrary.system.exceptions.EntityDuplicationException;
import org.niolikon.springbooklibrary.system.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@SpringBootTest
public class BookServiceIntegrationTest extends DBUnitTest {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private BookToBookViewConverter bookConverter;
    
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }
    
    private List<Book> fetchSnapshot() throws DataSetException {
        List<Book> bookList = new ArrayList<>();
        IDataSet dataSet = this.getDataSet();

        ITable authorTable = dataSet.getTable("author");
        HashMap<Long, Author> authorMap = new HashMap<>();
        for(int i=0; i<authorTable.getRowCount(); ++i) {
            Author author = new Author();
            author.setId(Long.valueOf((String) authorTable.getValue(i, "id")));
            author.setName((String) authorTable.getValue(i, "name"));
            author.setSurname((String) authorTable.getValue(i, "surname"));
            
            authorMap.put(author.getId(), author);
        }

        ITable publisherTable = dataSet.getTable("publisher");
        HashMap<Long, Publisher> publisherMap = new HashMap<>();
        for(int i=0; i<publisherTable.getRowCount(); ++i) {
            Publisher publisher = new Publisher();
            publisher.setId(Long.valueOf((String) publisherTable.getValue(i, "id")));
            publisher.setName((String) publisherTable.getValue(i, "name"));
            
            publisherMap.put(publisher.getId(), publisher);
        }

        ITable bookTable = dataSet.getTable("book");
        for(int i=0; i<bookTable.getRowCount(); ++i) {
            Book book = new Book();
            book.setId( Long.valueOf((String) bookTable.getValue(i, "id")));
            book.setTitle( (String) bookTable.getValue(i, "title"));
            book.setQuantity( Integer.valueOf((String) bookTable.getValue(i, "quantity")));
            
            Long author_id = Long.valueOf((String) bookTable.getValue(i, "author_id"));
            book.setAuthor(authorMap.get(author_id));
            
            Long publisher_id = Long.valueOf((String) bookTable.getValue(i, "publisher_id"));
            book.setPublisher(publisherMap.get(publisher_id));
            
            bookList.add(book);
        }
        
        return bookList;
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void create_NewBookGiven_ShouldValueBookId() throws DataSetException {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(snapshot.size() - 1);
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Test book title");
        bookRequest.setQuantity(2);
        bookRequest.setAuthorId(book.getAuthor().getId());
        bookRequest.setPublisherId(book.getPublisher().getId());
        
        BookView bookCreated = bookService.create(bookRequest);
        
        Assertions.assertNotNull(bookCreated.getId());
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void create_NewBookGiven_ShouldCreateNewBook() throws DataSetException {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(0);
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Brand new title");
        bookRequest.setQuantity(3);
        bookRequest.setAuthorId(book.getAuthor().getId());
        bookRequest.setPublisherId(book.getPublisher().getId());
        
        BookView bookCreated = bookService.create(bookRequest);
        
        List<BookView> books = bookService.findAllBooks(Pageable.unpaged()).getContent();
        Assertions.assertTrue(books.contains(bookCreated));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void create_ExistentBookGiven_ShouldThrowException() throws DataSetException {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(snapshot.size() - 1);
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle(book.getTitle());
        bookRequest.setQuantity(book.getQuantity());
        bookRequest.setAuthorId(book.getAuthor().getId());
        bookRequest.setPublisherId(book.getPublisher().getId());
        
        Assertions.assertThrows(EntityDuplicationException.class, () -> bookService.create(bookRequest));
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void create_NonExistentAuthorGiven_ShouldThrowException() throws DataSetException {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(0);
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Title for no author");
        bookRequest.setQuantity(1);
        bookRequest.setAuthorId(1000L);
        bookRequest.setPublisherId(book.getPublisher().getId());
        
        Assertions.assertThrows(Exception.class, () -> bookService.create(bookRequest));
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void create_NonExistentPublisherGiven_ShouldThrowException() throws DataSetException {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(snapshot.size() - 1);
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Title for no publisher");
        bookRequest.setQuantity(1);
        bookRequest.setAuthorId(book.getAuthor().getId());
        bookRequest.setPublisherId(1000L);
        
        Assertions.assertThrows(Exception.class, () -> bookService.create(bookRequest));
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getBook_ExistingBookIdGiven_ShouldReturnBook() throws DataSetException {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(1);
        BookView expected = bookConverter.convert(book);

        BookView bookGot = bookService.getBook(book.getId());
        
        Assertions.assertNotNull(bookGot);
        Assertions.assertEquals(expected.getId(), bookGot.getId());
        Assertions.assertEquals(expected.getTitle(), bookGot.getTitle());
        Assertions.assertEquals(expected.getQuantity(), bookGot.getQuantity());
        Assertions.assertEquals(expected.getAuthor().getId(), bookGot.getAuthor().getId());
        Assertions.assertEquals(expected.getPublisher().getId(), bookGot.getPublisher().getId());
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getBook_NonExistingBookIdGiven_ShouldReturnEmptyOptional() throws DataSetException {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(snapshot.size() - 1);
        
        Assertions.assertThrows(EntityNotFoundException.class, () -> bookService.getBook(book.getId() + 1));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    void findAllBooks_DefaultDatasetGiven_ShouldProvideCompleteList() throws DataSetException {
    	List<Book> snapshot = this.fetchSnapshot();
    	List<BookView> expected = snapshot.stream()
    	        .map( book -> bookService.getBook(book.getId()))
    	        .collect(Collectors.toList());
        
        List<BookView> books = bookService.findAllBooks(Pageable.unpaged()).getContent();

        Assertions.assertEquals(expected.size(), books.size());
        Assertions.assertTrue(books.containsAll(expected));
    }
    
    @Test
    public void update_ExistentBookGiven_ShouldModifyBook() throws DataSetException {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(0);
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Modified Title");
        bookRequest.setQuantity(1000);
        bookRequest.setAuthorId(book.getAuthor().getId());
        bookRequest.setPublisherId(book.getPublisher().getId());
        
        BookView bookModified = bookService.update(book, bookRequest);

        List<BookView> books = bookService.findAllBooks(Pageable.unpaged()).getContent();
        Assertions.assertTrue(books.contains(bookModified));
        Assertions.assertEquals(bookRequest.getTitle(), bookModified.getTitle());
        Assertions.assertEquals(bookRequest.getQuantity(), bookModified.getQuantity());
        Assertions.assertEquals(bookRequest.getAuthorId(), bookModified.getAuthor().getId());
        Assertions.assertEquals(bookRequest.getPublisherId(), bookModified.getPublisher().getId());
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void update_NonExistentAuthorGiven_ShouldThrowException() throws DataSetException {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(snapshot.size() - 1);
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Modified Title");
        bookRequest.setQuantity(book.getQuantity());
        bookRequest.setAuthorId(1000L);
        bookRequest.setPublisherId(book.getPublisher().getId());
        
        Assertions.assertThrows(Exception.class, () -> bookService.update(book, bookRequest));
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void update_NonExistentPublisherGiven_ShouldThrowException() throws DataSetException {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(snapshot.size() - 1);
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Modified Title");
        bookRequest.setQuantity(book.getQuantity());
        bookRequest.setAuthorId(book.getAuthor().getId());
        bookRequest.setPublisherId(1000L);
        
        Assertions.assertThrows(Exception.class, () -> bookService.update(book, bookRequest));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_ExistentBookGiven_ShouldRemoveBook() throws DataSetException {
		List<Book> snapshot = this.fetchSnapshot();
		Book book = snapshot.get(snapshot.size() - 1);
		BookView bookView = bookConverter.convert(book);

		bookService.delete(book.getId());
        
        List<BookView> bookViews = bookService.findAllBooks(Pageable.unpaged()).getContent();
        Assertions.assertFalse(bookViews.contains(bookView));
		Assertions.assertThrows(EntityNotFoundException.class, () -> bookService.getBook(book.getId()));
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_NonExistentBookGiven_ShouldThrowException() throws DataSetException {
    	List<Book> snapshot = this.fetchSnapshot();
		Book book = snapshot.get(snapshot.size() - 1);

		Assertions.assertThrows(EntityNotFoundException.class, () -> bookService.delete(book.getId() + 1));
    }
}
