package org.niolikon.springbooklibrary.book;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.junit.jupiter.api.Test;
import org.niolikon.springbooklibrary.author.Author;
import org.niolikon.springbooklibrary.book.web.BookRequest;
import org.niolikon.springbooklibrary.commons.DBUnitTest;
import org.niolikon.springbooklibrary.publisher.Publisher;
import org.niolikon.springbooklibrary.system.exceptions.EntityDuplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIntegrationTest extends DBUnitTest {
    
    @Autowired
    private MockMvc mockMvc;

    private final String ADMIN_USERNAME     = "admin";
    private final String ADMIN_PASSWORD     = "admin";

    private final String USER_USERNAME      = "user";
    private final String USER_PASSWORD      = "user";
    
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
    public void get_ExistentIdGiven_AdminAuth_ShouldReturnBook() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(0);
        Long book_id = book.getId();
        
        mockMvc.perform(MockMvcRequestBuilders.get("/books/"+ book_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(book.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(book.getTitle()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").value(book.getQuantity()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.author.id").value(book.getAuthor().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.author.name").value(book.getAuthor().getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.author.surname").value(book.getAuthor().getSurname()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.publisher.id").value(book.getPublisher().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.publisher.name").value(book.getPublisher().getName()));
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void get_ExistentIdGiven_UserAuth_ShouldReturnBook() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(0);
        Long book_id = book.getId();
        
        mockMvc.perform(MockMvcRequestBuilders.get("/books/"+ book_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_USERNAME, USER_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(book.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(book.getTitle()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").value(book.getQuantity()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.author.id").value(book.getAuthor().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.author.name").value(book.getAuthor().getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.author.surname").value(book.getAuthor().getSurname()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.publisher.id").value(book.getPublisher().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.publisher.name").value(book.getPublisher().getName()));
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void get_ExistentIdGiven_NoAuth_ShouldReturnError() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(0);
        Long book_id = book.getId();
        
        mockMvc.perform(MockMvcRequestBuilders.get("/books/"+ book_id.toString())
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void get_NonExistentIdGiven_AdminAuth_ShouldReturnError() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(snapshot.size() - 1);
        Long book_outofboundId = book.getId() + 1;

        mockMvc.perform(MockMvcRequestBuilders.get("/books/"+ book_outofboundId.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(EntityNotFoundException.class.getSimpleName()));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getAll_AdminAuth_ShouldReturnAll() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Integer snapshot_lastIdx = snapshot.size() - 1;
        Book book_first = snapshot.get(0);
        Book book_last = snapshot.get(snapshot_lastIdx);
        
        mockMvc.perform(MockMvcRequestBuilders.get("/books")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(book_first.getId().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].title").value(book_first.getTitle()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].quantity").value(book_first.getQuantity()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].author.id").value(book_first.getAuthor().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].author.name").value(book_first.getAuthor().getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].author.surname").value(book_first.getAuthor().getSurname()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].publisher.id").value(book_first.getPublisher().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].publisher.name").value(book_first.getPublisher().getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].id").value(book_last.getId().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].title").value(book_last.getTitle()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].quantity").value(book_last.getQuantity()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].author.id").value(book_last.getAuthor().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].author.name").value(book_last.getAuthor().getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].author.surname").value(book_last.getAuthor().getSurname()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].publisher.id").value(book_last.getPublisher().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].publisher.name").value(book_last.getPublisher().getName()));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getAll_UserAuth_ShouldReturnAll() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Integer snapshot_lastIdx = snapshot.size() - 1;
        Book book_first = snapshot.get(0);
        Book book_last = snapshot.get(snapshot_lastIdx);
        
        mockMvc.perform(MockMvcRequestBuilders.get("/books")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_USERNAME, USER_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(book_first.getId().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].title").value(book_first.getTitle()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].quantity").value(book_first.getQuantity()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].author.id").value(book_first.getAuthor().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].author.name").value(book_first.getAuthor().getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].author.surname").value(book_first.getAuthor().getSurname()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].publisher.id").value(book_first.getPublisher().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].publisher.name").value(book_first.getPublisher().getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].id").value(book_last.getId().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].title").value(book_last.getTitle()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].quantity").value(book_last.getQuantity()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].author.id").value(book_last.getAuthor().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].author.name").value(book_last.getAuthor().getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].author.surname").value(book_last.getAuthor().getSurname()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].publisher.id").value(book_last.getPublisher().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].publisher.name").value(book_last.getPublisher().getName()));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getAll_NoAuth_ShouldReturnError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/books")
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void post_NewBookGiven_AdminAuth_ShouldCreateBook() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Integer snapshot_lastIdx = snapshot.size() - 1;
        Book book_first = snapshot.get(0);
        Book book_last = snapshot.get(snapshot_lastIdx);
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Test book name");
        bookRequest.setQuantity(100);
        bookRequest.setAuthorId(book_first.getAuthor().getId());
        bookRequest.setPublisherId(book_last.getPublisher().getId());
        
        mockMvc.perform(MockMvcRequestBuilders.post("/books")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(bookRequest.getTitle()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").value(bookRequest.getQuantity()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.author.id").value(book_first.getAuthor().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.author.name").value(book_first.getAuthor().getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.author.surname").value(book_first.getAuthor().getSurname()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.publisher.id").value(book_last.getPublisher().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.publisher.name").value(book_last.getPublisher().getName()));
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void post_NewBookGiven_UserAuth_ShouldReturnError() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Integer snapshot_lastIdx = snapshot.size() - 1;
        Book book_first = snapshot.get(0);
        Book book_last = snapshot.get(snapshot_lastIdx);
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Test book name");
        bookRequest.setQuantity(100);
        bookRequest.setAuthorId(book_first.getAuthor().getId());
        bookRequest.setPublisherId(book_last.getPublisher().getId());
        
        mockMvc.perform(MockMvcRequestBuilders.post("/books")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_USERNAME, USER_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isForbidden());
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void post_NewBookGiven_NoAuth_ShouldReturnError() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Integer snapshot_lastIdx = snapshot.size() - 1;
        Book book_first = snapshot.get(0);
        Book book_last = snapshot.get(snapshot_lastIdx);
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Test book name");
        bookRequest.setQuantity(100);
        bookRequest.setAuthorId(book_first.getAuthor().getId());
        bookRequest.setPublisherId(book_last.getPublisher().getId());
        
        mockMvc.perform(MockMvcRequestBuilders.post("/books")
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void post_ExistentBookGiven_AdminAuth_ShouldReturnError() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(0);
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle(book.getTitle());
        bookRequest.setQuantity(book.getQuantity());
        bookRequest.setAuthorId(book.getAuthor().getId());
        bookRequest.setPublisherId(book.getPublisher().getId());
        
        mockMvc.perform(MockMvcRequestBuilders.post("/books")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isConflict())
        .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(EntityDuplicationException.class.getSimpleName()));
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_ExistentIdGiven_AdminAuth_ShouldDeleteBook() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(0);
        Long book_id = book.getId();
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/books/"+ book_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNoContent());
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_ExistentIdGiven_UserAuth_ShouldReturnError() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(0);
        Long book_id = book.getId();
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/books/"+ book_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_USERNAME, USER_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isForbidden());
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_ExistentIdGiven_NoAuth_ShouldReturnError() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(0);
        Long book_id = book.getId();
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/books/"+ book_id.toString())
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_NonExistentIdGiven_AdminAuth_ShouldReturnError() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(snapshot.size() - 1);
        Long book_outofboundId = book.getId() + 1;

        mockMvc.perform(MockMvcRequestBuilders.delete("/books/"+ book_outofboundId.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(EntityNotFoundException.class.getSimpleName()));
    }


    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_ExistentBookGiven_AdminAuth_ShouldModifyBook() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(0);
        Long book_id = book.getId();
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Modified name");
        bookRequest.setQuantity(book.getQuantity());
        bookRequest.setAuthorId(book.getAuthor().getId());
        bookRequest.setPublisherId(book.getPublisher().getId());
        
        mockMvc.perform(MockMvcRequestBuilders.put("/books/" + book_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(book_id))
        .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(bookRequest.getTitle()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").value(bookRequest.getQuantity()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.author.id").value(book.getAuthor().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.author.name").value(book.getAuthor().getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.author.surname").value(book.getAuthor().getSurname()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.publisher.id").value(book.getPublisher().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.publisher.name").value(book.getPublisher().getName()));
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_ExistentBookGiven_UserAuth_ShouldReturnError() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(0);
        Long book_id = book.getId();
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Modified name");
        bookRequest.setQuantity(book.getQuantity());
        bookRequest.setAuthorId(book.getAuthor().getId());
        bookRequest.setPublisherId(book.getPublisher().getId());
        
        mockMvc.perform(MockMvcRequestBuilders.put("/books/" + book_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_USERNAME, USER_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isForbidden());
    }

    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_ExistentBookGiven_NoAuth_ShouldReturnError() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(0);
        Long book_id = book.getId();
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Modified name");
        bookRequest.setQuantity(book.getQuantity());
        bookRequest.setAuthorId(book.getAuthor().getId());
        bookRequest.setPublisherId(book.getPublisher().getId());
        
        mockMvc.perform(MockMvcRequestBuilders.put("/books/" + book_id.toString())
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-books.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_NonExistentBookGiven_AdminAuth_ShouldReturnError() throws Exception {
        List<Book> snapshot = this.fetchSnapshot();
        Book book = snapshot.get(snapshot.size() - 1);
        Long book_outofboundId = book.getId() + 1L;
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle(book.getTitle());
        
        mockMvc.perform(MockMvcRequestBuilders.put("/books/" + book_outofboundId.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(EntityNotFoundException.class.getSimpleName()));
    }
    
    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
