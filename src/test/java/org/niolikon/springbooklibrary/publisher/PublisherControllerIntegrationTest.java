package org.niolikon.springbooklibrary.publisher;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.junit.jupiter.api.Test;
import org.niolikon.springbooklibrary.publisher.web.PublisherRequest;
import org.niolikon.springbooklibrary.commons.DBUnitTest;
import org.niolikon.springbooklibrary.system.exceptions.EntityDuplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
public class PublisherControllerIntegrationTest extends DBUnitTest {
    
    @Autowired
    private MockMvc wockMvc;
    
    private List<Publisher> fetchSnapshot() throws DataSetException {
        List<Publisher> publisherList = new ArrayList<>();
        
        IDataSet dataSet = this.getDataSet();
        ITable publisherTable = dataSet.getTable("publisher");
        
        for(int i=0; i< publisherTable.getRowCount(); ++i) {
            Publisher publisher = new Publisher();
            publisher.setId( Long.valueOf((String) publisherTable.getValue(i, "id")));
            publisher.setName( (String) publisherTable.getValue(i, "name"));
            
            publisherList.add(publisher);
        }
        
        return publisherList;
    }

    @Test
    @DatabaseSetup(value = "/dataset-publishers.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void get_ExistentIdGiven_ShouldReturnPublisher() throws Exception {
        List<Publisher> snapshot = this.fetchSnapshot();
        Publisher publisher = snapshot.get(0);
        Long publisher_id = publisher.getId();
        
        wockMvc.perform(MockMvcRequestBuilders.get("/publishers/"+ publisher_id.toString())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(publisher.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(publisher.getName()));
    }

    @Test
    @DatabaseSetup(value = "/dataset-publishers.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void get_NonExistentIdGiven_ShouldReturnError() throws Exception {
        List<Publisher> snapshot = this.fetchSnapshot();
        Publisher publisher = snapshot.get(snapshot.size() - 1);
        Long publisher_outofboundId = publisher.getId() + 1;

        wockMvc.perform(MockMvcRequestBuilders.get("/publishers/"+ publisher_outofboundId.toString())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(EntityNotFoundException.class.getSimpleName()));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-publishers.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getAll_ExistentIdGiven_ShouldReturnAll() throws Exception {
        List<Publisher> snapshot = this.fetchSnapshot();
        Integer snapshot_lastIdx = snapshot.size() - 1;
        Publisher publisher_first = snapshot.get(0);
        Publisher publisher_last = snapshot.get(snapshot_lastIdx);
        
        wockMvc.perform(MockMvcRequestBuilders.get("/publishers")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(publisher_first.getId().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value(publisher_first.getName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].id").value(publisher_last.getId().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content["+snapshot_lastIdx+"].name").value(publisher_last.getName()));
    }

    @Test
    @DatabaseSetup(value = "/dataset-publishers.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void post_NewPublisherGiven_ShouldCreatePublisher() throws Exception {
        PublisherRequest publisherRequest = new PublisherRequest();
        publisherRequest.setName("Test publisher name");
        
        wockMvc.perform(MockMvcRequestBuilders.post("/publishers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(publisherRequest))
        .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(publisherRequest.getName()));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-publishers.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void post_ExistentPublisherGiven_ShouldReturnError() throws Exception {
        List<Publisher> snapshot = this.fetchSnapshot();
        Publisher publisher = snapshot.get(0);
        PublisherRequest publisherRequest = new PublisherRequest();
        publisherRequest.setName(publisher.getName());
        
        wockMvc.perform(MockMvcRequestBuilders.post("/publishers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(publisherRequest))
        .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isConflict())
        .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(EntityDuplicationException.class.getSimpleName()));
    }

    @Test
    @DatabaseSetup(value = "/dataset-publishers.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_ExistentIdGiven_ShouldReturnPublisher() throws Exception {
        List<Publisher> snapshot = this.fetchSnapshot();
        Publisher publisher = snapshot.get(0);
        Long publisher_id = publisher.getId();
        
        wockMvc.perform(MockMvcRequestBuilders.delete("/publishers/"+ publisher_id.toString())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNoContent());
    }

    @Test
    @DatabaseSetup(value = "/dataset-publishers.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_NonExistentIdGiven_ShouldReturnError() throws Exception {
        List<Publisher> snapshot = this.fetchSnapshot();
        Publisher publisher = snapshot.get(snapshot.size() - 1);
        Long publisher_outofboundId = publisher.getId() + 1;

        wockMvc.perform(MockMvcRequestBuilders.delete("/publishers/"+ publisher_outofboundId.toString())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(EntityNotFoundException.class.getSimpleName()));
    }


    @Test
    @DatabaseSetup(value = "/dataset-publishers.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_ExistentPublisherGiven_ShouldModifyPublisher() throws Exception {
        List<Publisher> snapshot = this.fetchSnapshot();
        Publisher publisher = snapshot.get(0);
        Long publisher_id = publisher.getId();
        PublisherRequest publisherRequest = new PublisherRequest();
        publisherRequest.setName("Modified name");
        
        wockMvc.perform(MockMvcRequestBuilders.put("/publishers/" + publisher_id.toString())
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(publisherRequest))
        .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(publisher_id))
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(publisherRequest.getName()));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-publishers.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_NonExistentPublisherGiven_ShouldReturnError() throws Exception {
        List<Publisher> snapshot = this.fetchSnapshot();
        Publisher publisher = snapshot.get(snapshot.size() - 1);
        Long publisher_outofboundId = publisher.getId() + 1L;
        PublisherRequest publisherRequest = new PublisherRequest();
        publisherRequest.setName(publisher.getName());
        
        wockMvc.perform(MockMvcRequestBuilders.put("/publishers/" + publisher_outofboundId.toString())
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(publisherRequest))
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
