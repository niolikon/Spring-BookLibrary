package org.niolikon.springbooklibrary.publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.niolikon.springbooklibrary.publisher.converter.PublisherToPublisherViewConverter;
import org.niolikon.springbooklibrary.publisher.web.PublisherRequest;
import org.niolikon.springbooklibrary.publisher.web.PublisherView;
import org.niolikon.springbooklibrary.commons.DBUnitTest;
import org.niolikon.springbooklibrary.system.exceptions.EntityDuplicationException;
import org.niolikon.springbooklibrary.system.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@SpringBootTest
public class PublisherServiceIntegrationTest extends DBUnitTest {
    
    @Autowired
    private PublisherService publisherService;
    
    @Autowired
    private PublisherToPublisherViewConverter publisherConverter;
    
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }
    
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
    public void create_NewPublisherGiven_ShouldValuePublisherId() throws DataSetException {
        PublisherRequest publisherRequest = new PublisherRequest();
        publisherRequest.setName("Test publisher name");
        
        PublisherView publisherCreated = publisherService.create(publisherRequest);
        
        Assertions.assertNotNull(publisherCreated.getId());
    }

    @Test
    @DatabaseSetup(value = "/dataset-publishers.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void create_NewPublisherGiven_ShouldCreateNewPublisher() throws DataSetException {
        PublisherRequest publisherRequest = new PublisherRequest();
        publisherRequest.setName("Brand new name");
        
        PublisherView publisherCreated = publisherService.create(publisherRequest);
        
        List<PublisherView> publishers = publisherService.findAllPublishers(Pageable.unpaged()).getContent();
        Assertions.assertTrue(publishers.contains(publisherCreated));
    }

    @Test
    @DatabaseSetup(value = "/dataset-publishers.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void create_ExistentPublisherGiven_ShouldThrowException() throws DataSetException {
        List<Publisher> snapshot = this.fetchSnapshot();
        Publisher publisher = snapshot.get(snapshot.size() - 1);
        PublisherRequest publisherRequest = new PublisherRequest();
        publisherRequest.setName(publisher.getName());
        
        Assertions.assertThrows(EntityDuplicationException.class, () -> publisherService.create(publisherRequest));
    }

    @Test
    @DatabaseSetup(value = "/dataset-publishers.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getPublisher_ExistingPublisherIdGiven_ShouldReturnPublisher() throws DataSetException {
        List<Publisher> snapshot = this.fetchSnapshot();
        Publisher publisher = snapshot.get(0);
        PublisherView expected = publisherConverter.convert(publisher);

        PublisherView publisherGot = publisherService.getPublisher(publisher.getId());
        
        Assertions.assertNotNull(publisherGot);
        Assertions.assertEquals(expected.getId(), publisherGot.getId());
        Assertions.assertEquals(expected.getName(), publisherGot.getName());
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-publishers.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getPublisher_NonExistingPublisherIdGiven_ShouldReturnEmptyOptional() throws DataSetException {
        List<Publisher> snapshot = this.fetchSnapshot();
        Publisher publisher = snapshot.get(snapshot.size() - 1);
        
        Assertions.assertThrows(EntityNotFoundException.class, () -> publisherService.getPublisher(publisher.getId() + 1));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-publishers.xml", type = DatabaseOperation.CLEAN_INSERT)
    void findAllPublishers_DefaultDatasetGiven_ShouldProvideCompleteList() throws DataSetException {
    	List<Publisher> snapshot = this.fetchSnapshot();
    	List<PublisherView> expected = snapshot.stream()
    	        .map( publisher -> publisherService.getPublisher(publisher.getId()))
    	        .collect(Collectors.toList());
        
        List<PublisherView> publishers = publisherService.findAllPublishers(Pageable.unpaged()).getContent();

        Assertions.assertEquals(expected.size(), publishers.size());
        Assertions.assertTrue(publishers.containsAll(expected));
    }
    
    @Test
    public void update_ExistentPublisherGiven_ShouldModifyPublisher() throws DataSetException {
        List<Publisher> snapshot = this.fetchSnapshot();
        Publisher publisher = snapshot.get(0);
        PublisherRequest publisherRequest = new PublisherRequest();
        publisherRequest.setName("Modified Name");
        
        PublisherView publisherModified = publisherService.update(publisher, publisherRequest);
        
        List<PublisherView> publishers = publisherService.findAllPublishers(Pageable.unpaged()).getContent();
        Assertions.assertTrue(publishers.contains(publisherModified));
        Assertions.assertEquals(publisherRequest.getName(), publisherModified.getName());
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-publishers.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_ExistentPublisherGiven_ShouldRemovePublisher() throws DataSetException {
		List<Publisher> snapshot = this.fetchSnapshot();
		Publisher publisher = snapshot.get(snapshot.size() - 1);
		PublisherView publisherView = publisherConverter.convert(publisher);

		publisherService.delete(publisher.getId());
        
        List<PublisherView> publisherViews = publisherService.findAllPublishers(Pageable.unpaged()).getContent();
        Assertions.assertFalse(publisherViews.contains(publisherView));
		Assertions.assertThrows(EntityNotFoundException.class, () -> publisherService.getPublisher(publisher.getId()));
    }

    @Test
    @DatabaseSetup(value = "/dataset-publishers.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_NonExistentPublisherGiven_ShouldThrowException() throws DataSetException {
    	List<Publisher> snapshot = this.fetchSnapshot();
		Publisher publisher = snapshot.get(snapshot.size() - 1);

		Assertions.assertThrows(EntityNotFoundException.class, () -> publisherService.delete(publisher.getId() + 1));
    }
}
