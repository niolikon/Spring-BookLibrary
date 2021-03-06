package org.niolikon.springbooklibrary.author;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.junit.jupiter.api.Test;
import org.niolikon.springbooklibrary.author.web.AuthorRequest;
import org.niolikon.springbooklibrary.commons.DBUnitTest;
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
public class AuthorControllerIntegrationTest extends DBUnitTest {
    
    @Autowired
    private MockMvc mockMvc;

    private final String ADMIN_USERNAME     = "admin";
    private final String ADMIN_PASSWORD     = "admin";

    private final String USER_USERNAME      = "user";
    private final String USER_PASSWORD      = "user";
    
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
    public void get_ExistentIdGiven_AdminAuth_ShouldReturnAuthor() throws Exception {
        List<Author> snapshot = this.fetchSnapshot();
        Author author = snapshot.get(0);
        Long author_id = author.getId();
        
        mockMvc.perform(MockMvcRequestBuilders.get("/authors/"+ author_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(author.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(author.getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value(author.getSurname()));
    }

    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void get_ExistentIdGiven_UserAuth_ShouldReturnAuthor() throws Exception {
        List<Author> snapshot = this.fetchSnapshot();
        Author author = snapshot.get(0);
        Long author_id = author.getId();
        
        mockMvc.perform(MockMvcRequestBuilders.get("/authors/"+ author_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_USERNAME, USER_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(author.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(author.getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value(author.getSurname()));
    }

    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void get_ExistentIdGiven_NoAuth_ShouldReturnError() throws Exception {
        List<Author> snapshot = this.fetchSnapshot();
        Author author = snapshot.get(0);
        Long author_id = author.getId();
        
        mockMvc.perform(MockMvcRequestBuilders.get("/authors/"+ author_id.toString())
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void get_NonExistentIdGiven_AdminAuth_ShouldReturnError() throws Exception {
        List<Author> snapshot = this.fetchSnapshot();
        Author author = snapshot.get(snapshot.size() - 1);
        Long author_outofboundId = author.getId() + 1;

        mockMvc.perform(MockMvcRequestBuilders.get("/authors/"+ author_outofboundId.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(EntityNotFoundException.class.getSimpleName()));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getAll_AdminAuth_ShouldReturnAll() throws Exception {
        List<Author> snapshot = this.fetchSnapshot();
        Integer snapshot_lastIdx = snapshot.size() - 1;
        Author author_first = snapshot.get(0);
        Author author_last = snapshot.get(snapshot_lastIdx);
        
        mockMvc.perform(MockMvcRequestBuilders.get("/authors")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(author_first.getId().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value(author_first.getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].surname").value(author_first.getSurname()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].id").value(author_last.getId().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].name").value(author_last.getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].surname").value(author_last.getSurname()));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getAll_UserAuth_ShouldReturnAll() throws Exception {
        List<Author> snapshot = this.fetchSnapshot();
        Integer snapshot_lastIdx = snapshot.size() - 1;
        Author author_first = snapshot.get(0);
        Author author_last = snapshot.get(snapshot_lastIdx);
        
        mockMvc.perform(MockMvcRequestBuilders.get("/authors")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(author_first.getId().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value(author_first.getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].surname").value(author_first.getSurname()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].id").value(author_last.getId().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].name").value(author_last.getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].surname").value(author_last.getSurname()));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getAll_NoAuth_ShouldReturnError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/authors")
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void post_NewAuthorGiven_AdminAuth_ShouldCreateAuthor() throws Exception {
        AuthorRequest authorRequest = new AuthorRequest();
        authorRequest.setName("Test author name");
        authorRequest.setSurname("Test author surname");
        
        mockMvc.perform(MockMvcRequestBuilders.post("/authors")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(authorRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(authorRequest.getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value(authorRequest.getSurname()));
    }

    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void post_NewAuthorGiven_UserAuth_ShouldReturnError() throws Exception {
        AuthorRequest authorRequest = new AuthorRequest();
        authorRequest.setName("Test author name");
        authorRequest.setSurname("Test author surname");
        
        mockMvc.perform(MockMvcRequestBuilders.post("/authors")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_USERNAME, USER_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(authorRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isForbidden());
    }

    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void post_NewAuthorGiven_NoAuth_ShouldReturnError() throws Exception {
        AuthorRequest authorRequest = new AuthorRequest();
        authorRequest.setName("Test author name");
        authorRequest.setSurname("Test author surname");
        
        mockMvc.perform(MockMvcRequestBuilders.post("/authors")
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(authorRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void post_ExistentAuthorGiven_AdminAuth_ShouldReturnError() throws Exception {
        List<Author> snapshot = this.fetchSnapshot();
        Author author = snapshot.get(0);
        AuthorRequest authorRequest = new AuthorRequest();
        authorRequest.setName(author.getName());
        authorRequest.setSurname(author.getSurname());
        
        mockMvc.perform(MockMvcRequestBuilders.post("/authors")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(authorRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isConflict())
        .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(EntityDuplicationException.class.getSimpleName()));
    }

    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_ExistentIdGiven_AdminAuth_ShouldDeleteAuthor() throws Exception {
        List<Author> snapshot = this.fetchSnapshot();
        Author author = snapshot.get(0);
        Long author_id = author.getId();
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/authors/"+ author_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNoContent());
    }

    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_ExistentIdGiven_UserAuth_ShouldReturnError() throws Exception {
        List<Author> snapshot = this.fetchSnapshot();
        Author author = snapshot.get(0);
        Long author_id = author.getId();
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/authors/"+ author_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_USERNAME, USER_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isForbidden());
    }

    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_ExistentIdGiven_NoAuth_ShouldReturnError() throws Exception {
        List<Author> snapshot = this.fetchSnapshot();
        Author author = snapshot.get(0);
        Long author_id = author.getId();
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/authors/"+ author_id.toString())
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_NonExistentIdGiven_AdminAuth_ShouldReturnError() throws Exception {
        List<Author> snapshot = this.fetchSnapshot();
        Author author = snapshot.get(snapshot.size() - 1);
        Long author_outofboundId = author.getId() + 1;

        mockMvc.perform(MockMvcRequestBuilders.delete("/authors/"+ author_outofboundId.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(EntityNotFoundException.class.getSimpleName()));
    }


    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_ExistentAuthorGiven_AdminAuth_ShouldModifyAuthor() throws Exception {
        List<Author> snapshot = this.fetchSnapshot();
        Author author = snapshot.get(0);
        Long author_id = author.getId();
        AuthorRequest authorRequest = new AuthorRequest();
        authorRequest.setName("Modified name");
        authorRequest.setSurname("Modified surname");
        
        mockMvc.perform(MockMvcRequestBuilders.put("/authors/" + author_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(authorRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(author_id))
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(authorRequest.getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value(authorRequest.getSurname()));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_ExistentAuthorGiven_UserAuth_ShouldReturnError() throws Exception {
        List<Author> snapshot = this.fetchSnapshot();
        Author author = snapshot.get(0);
        Long author_id = author.getId();
        AuthorRequest authorRequest = new AuthorRequest();
        authorRequest.setName("Modified name");
        authorRequest.setSurname("Modified surname");
        
        mockMvc.perform(MockMvcRequestBuilders.put("/authors/" + author_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_USERNAME, USER_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(authorRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isForbidden());
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_ExistentAuthorGiven_NoAuth_ShouldReturnError() throws Exception {
        List<Author> snapshot = this.fetchSnapshot();
        Author author = snapshot.get(0);
        Long author_id = author.getId();
        AuthorRequest authorRequest = new AuthorRequest();
        authorRequest.setName("Modified name");
        authorRequest.setSurname("Modified surname");
        
        mockMvc.perform(MockMvcRequestBuilders.put("/authors/" + author_id.toString())
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(authorRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-authors.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_NonExistentAuthorGiven_AdminAuth_ShouldReturnError() throws Exception {
        List<Author> snapshot = this.fetchSnapshot();
        Author author = snapshot.get(snapshot.size() - 1);
        Long author_outofboundId = author.getId() + 1L;
        AuthorRequest authorRequest = new AuthorRequest();
        authorRequest.setName(author.getName());
        authorRequest.setSurname(author.getSurname());
        
        mockMvc.perform(MockMvcRequestBuilders.put("/authors/" + author_outofboundId.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(authorRequest))
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
