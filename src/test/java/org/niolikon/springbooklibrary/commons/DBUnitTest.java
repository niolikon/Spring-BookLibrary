package org.niolikon.springbooklibrary.commons;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

@RunWith(SpringRunner.class)
@TestExecutionListeners({
        TransactionalTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class
})
public abstract class DBUnitTest {
    
    @Autowired
    protected DataSource dataSource;
    
    private IDatabaseTester databaseTester;

	@BeforeEach
    public void setUp() throws Exception {
		databaseTester = new DataSourceDatabaseTester(dataSource);
        IDataSet dataSet = new FlatXmlDataSet(getClass().getResource("/dataset-default.xml"));
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
    }
	
	@AfterEach
	public void tearDown() throws Exception {
		databaseTester.onTearDown();
	}

    public IDataSet getDataSet() {
    	IDataSet dataset = databaseTester.getDataSet();
		return dataset;
	}
    
}
