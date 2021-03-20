package org.niolikon.springbooklibrary.author;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.niolikon.springbooklibrary.author.converter.AuthorToAuthorViewConverter;
import org.niolikon.springbooklibrary.author.web.AuthorRequest;
import org.niolikon.springbooklibrary.author.web.AuthorView;
import org.niolikon.springbooklibrary.commons.DBUnitTest;
import org.niolikon.springbooklibrary.system.exceptions.EntityDuplicationException;
import org.niolikon.springbooklibrary.system.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@SpringBootTest
public class AuthorServiceIntegrationTest extends DBUnitTest {
    
    @Autowired
    private AuthorService authorService;
    
    @Autowired
    private AuthorToAuthorViewConverter authorConverter;
    
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }
    
    private List<Author> fetchSnapshot() throws DataSetException {
    	List<Author> authorList = new ArrayList<>();
    	
    	IDataSet dataSet = this.getDataSet();
    	ITable authorTable = dataSet.getTable("author");
    	
    	for(int i=0; i< authorTable.getRowCount(); ++i) {
    		Author author = new Author();
    		author.setId( Long.valueOf((String) authorTable.getValue(i, "id")));
    		author.setName( (String) authorTable.getValue(i, "name"));
    		author.setSurname( (String) authorTable.getValue(i, "surname"));
    		
    		authorList.add(author);
    	}
    	
    	return authorList;
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void create_NewAuthorGiven_ShouldValueAuthorId() throws DataSetException {
        AuthorRequest authorRequest = new AuthorRequest();
        authorRequest.setName("Test author name");
        authorRequest.setSurname("Test author surname");
        
        AuthorView authorCreated = authorService.create(authorRequest);
        
        Assertions.assertNotNull(authorCreated.getId());
    }

    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void create_NewAuthorGiven_ShouldCreateNewAuthor() throws DataSetException {
        AuthorRequest authorRequest = new AuthorRequest();
        authorRequest.setName("Brand new name");
        authorRequest.setSurname("Brand new surname");
        
        AuthorView authorCreated = authorService.create(authorRequest);
        
        List<AuthorView> authors = authorService.findAllAuthors(Pageable.unpaged()).getContent();
        Assertions.assertTrue(authors.contains(authorCreated));
    }

    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void create_ExistentAuthorGiven_ShouldThrowException() throws DataSetException {
        List<Author> snapshot = this.fetchSnapshot();
        Author author = snapshot.get(snapshot.size() - 1);
        AuthorRequest authorRequest = new AuthorRequest();
        authorRequest.setName(author.getName());
        authorRequest.setSurname(author.getSurname());
        
        Assertions.assertThrows(EntityDuplicationException.class, () -> authorService.create(authorRequest));
    }

    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getAuthor_ExistingAuthorIdGiven_ShouldReturnAuthor() throws DataSetException {
        List<Author> snapshot = this.fetchSnapshot();
        Author author = snapshot.get(0);
        AuthorView expected = authorConverter.convert(author);

        AuthorView authorGot = authorService.getAuthor(author.getId());
        
        Assertions.assertNotNull(authorGot);
        Assertions.assertEquals(expected.getId(), authorGot.getId());
        Assertions.assertEquals(expected.getName(), authorGot.getName());
        Assertions.assertEquals(expected.getSurname(), authorGot.getSurname());
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getAuthor_NonExistingAuthorIdGiven_ShouldReturnEmptyOptional() throws DataSetException {
        List<Author> snapshot = this.fetchSnapshot();
        Author author = snapshot.get(snapshot.size() - 1);
        
        Assertions.assertThrows(EntityNotFoundException.class, () -> authorService.getAuthor(author.getId() + 1));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    void findAllAuthors_DefaultDatasetGiven_ShouldProvideCompleteList() throws DataSetException {
    	List<Author> snapshot = this.fetchSnapshot();
    	List<AuthorView> expected = snapshot.stream()
    	        .map( author -> authorService.getAuthor(author.getId()))
    	        .collect(Collectors.toList());
        
        List<AuthorView> authors = authorService.findAllAuthors(Pageable.unpaged()).getContent();

        Assertions.assertEquals(expected.size(), authors.size());
        Assertions.assertTrue(authors.containsAll(expected));
    }
    
    @Test
    public void update_ExistentAuthorGiven_ShouldModifyAuthor() throws DataSetException {
        List<Author> snapshot = this.fetchSnapshot();
        Author author = snapshot.get(0);
        AuthorRequest authorRequest = new AuthorRequest();
        authorRequest.setName("Modified Name");
        authorRequest.setSurname("Modified Surname");
        
        AuthorView authorModified = authorService.update(author, authorRequest);
        
        List<AuthorView> authors = authorService.findAllAuthors(Pageable.unpaged()).getContent();
        Assertions.assertTrue(authors.contains(authorModified));
        Assertions.assertEquals(authorRequest.getName(), authorModified.getName());
        Assertions.assertEquals(authorRequest.getSurname(), authorModified.getSurname());
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_ExistentAuthorGiven_ShouldRemoveAuthor() throws DataSetException {
		List<Author> snapshot = this.fetchSnapshot();
		Author author = snapshot.get(snapshot.size() - 1);
		AuthorView authorView = authorConverter.convert(author);

		authorService.delete(author.getId());
        
        List<AuthorView> authorViews = authorService.findAllAuthors(Pageable.unpaged()).getContent();
        Assertions.assertFalse(authorViews.contains(authorView));
		Assertions.assertThrows(EntityNotFoundException.class, () -> authorService.getAuthor(author.getId()));
    }

    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_NonExistentAuthorGiven_ShouldThrowException() throws DataSetException {
    	List<Author> snapshot = this.fetchSnapshot();
		Author author = snapshot.get(snapshot.size() - 1);

		Assertions.assertThrows(EntityNotFoundException.class, () -> authorService.delete(author.getId() + 1));
    }
}
