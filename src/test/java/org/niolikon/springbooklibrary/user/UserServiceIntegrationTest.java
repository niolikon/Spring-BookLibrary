package org.niolikon.springbooklibrary.user;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.niolikon.springbooklibrary.author.Author;
import org.niolikon.springbooklibrary.book.Book;
import org.niolikon.springbooklibrary.book.web.BookRequest;
import org.niolikon.springbooklibrary.user.converter.UserToUserViewConverter;
import org.niolikon.springbooklibrary.user.web.UserRequest;
import org.niolikon.springbooklibrary.user.web.UserView;
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
public class UserServiceIntegrationTest extends DBUnitTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserToUserViewConverter userConverter;
    
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }
    
    private List<User> fetchSnapshot() throws DataSetException {
        List<User> userList = new ArrayList<>();
        IDataSet dataSet = this.getDataSet();

        ITable roleTable = dataSet.getTable("role");
        HashMap<Long, Role> roleMap = new HashMap<>();
        for(int i=0; i<roleTable.getRowCount(); ++i) {
            Role role = new Role();
            role.setId(Long.valueOf((String) roleTable.getValue(i, "id")));
            role.setName((String) roleTable.getValue(i, "name"));
            
            roleMap.put(role.getId(), role);
        }

        ITable userroleTable = dataSet.getTable("enduserrole");
        HashMap<Long, List<Long>> userroleMap = new HashMap<>();
        for(int i=0; i<userroleTable.getRowCount(); ++i) {
            Long userId = Long.valueOf((String) userroleTable.getValue(i, "enduser_id"));
            Long roleId = Long.valueOf((String) userroleTable.getValue(i, "role_id"));
            
            List<Long> userroles = userroleMap.get(userId);
            if (userroles == null) {
                userroles = new ArrayList<Long>();
                userroleMap.put(userId, userroles);
            }
            userroles.add(roleId);
        }

        ITable userTable = dataSet.getTable("enduser");
        for(int i=0; i<userTable.getRowCount(); ++i) {
            User user = new User();
            user.setId( Long.valueOf((String) userTable.getValue(i, "id")));
            user.setName( (String) userTable.getValue(i, "name"));
            user.setSurname( (String) userTable.getValue(i, "surname"));
            user.setUsername( (String) userTable.getValue(i, "username"));
            user.setPassword( (String) userTable.getValue(i, "password"));
            
            List<Long> userroles = userroleMap.get(user.getId());
            Set<Role> roles = userroles.stream()
                    .map(role_id -> roleMap.get(role_id))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
            
            userList.add(user);
        }
        
        return userList;
    }
    
    private UserRequest fetchFromEntity(User entity) {
        UserRequest request = new UserRequest();
        request.setName(entity.getName());
        request.setSurname(entity.getSurname());
        request.setUsername(entity.getUsername());
        request.setPassword(entity.getPassword());
        
        Set<String> roles = entity.getRoles().stream()
                .map( role -> role.getName())
                .collect(Collectors.toSet());
        request.setRoles(roles);
        
        return request;
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void create_NewUserGiven_ShouldValueUserId() throws DataSetException {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(snapshot.size() - 1);
        UserRequest userRequest = this.fetchFromEntity(user);
        userRequest.setName("Test user name");
        userRequest.setSurname("Test user surname");
        userRequest.setUsername("username1");
        userRequest.setPassword("password1");
        
        
        UserView userCreated = userService.create(userRequest);
        
        Assertions.assertNotNull(userCreated.getId());
    }

    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void create_NewUserGiven_ShouldCreateNewUser() throws DataSetException {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(0);
        UserRequest userRequest = this.fetchFromEntity(user);
        userRequest.setName("Brand new name");
        userRequest.setSurname("Brand new surname");
        userRequest.setUsername("username1");
        userRequest.setPassword("password1");
        
        UserView userCreated = userService.create(userRequest);
        
        List<UserView> users = userService.findAllUsers(Pageable.unpaged()).getContent();
        Assertions.assertTrue(users.contains(userCreated));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void create_ExistentUsernameGiven_ShouldThrowException() throws DataSetException {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(snapshot.size() - 1);
        UserRequest userRequest = this.fetchFromEntity(user);
        userRequest.setName("Brand new name");
        userRequest.setSurname("Brand new surname");
        userRequest.setUsername(user.getUsername());
        userRequest.setPassword("complexPassword");
        
        Assertions.assertThrows(EntityDuplicationException.class, () -> userService.create(userRequest));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void create_NonExistentRoleGiven_ShouldThrowException() throws DataSetException {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(snapshot.size() - 1);
        UserRequest userRequest = this.fetchFromEntity(user);
        userRequest.setName("Brand new name");
        userRequest.setSurname("Brand new surname");
        userRequest.setUsername("username1");
        userRequest.setPassword("password1");
        userRequest.setRoles(Set.of("non", "existent", "roles"));
        
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.create(userRequest));
    }

    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getUser_ExistingUserIdGiven_ShouldReturnUser() throws DataSetException {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(1);
        UserView expected = userConverter.convert(user);

        UserView userGot = userService.getUser(user.getId());
        
        Assertions.assertNotNull(userGot);
        Assertions.assertEquals(expected.getId(), userGot.getId());
        Assertions.assertEquals(expected.getName(), userGot.getName());
        Assertions.assertEquals(expected.getSurname(), userGot.getSurname());
        Assertions.assertEquals(expected.getUsername(), userGot.getUsername());
        Assertions.assertEquals(expected.getPassword(), userGot.getPassword());
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getUser_NonExistingUserIdGiven_ShouldReturnEmptyOptional() throws DataSetException {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(snapshot.size() - 1);
        
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getUser(user.getId() + 1));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    void findAllUsers_DefaultDatasetGiven_ShouldProvideCompleteList() throws DataSetException {
    	List<User> snapshot = this.fetchSnapshot();
    	List<UserView> expected = snapshot.stream()
    	        .map( user -> userService.getUser(user.getId()))
    	        .collect(Collectors.toList());
        
        List<UserView> users = userService.findAllUsers(Pageable.unpaged()).getContent();

        Assertions.assertEquals(expected.size(), users.size());
        Assertions.assertTrue(users.containsAll(expected));
    }
    
    @Test
    public void update_ExistentUserGiven_ShouldModifyUser() throws DataSetException {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(0);
        UserRequest userRequest = this.fetchFromEntity(user);
        userRequest.setName("Modified Name");
        userRequest.setSurname("Modified Surname");
        
        UserView userModified = userService.update(user, userRequest);

        List<UserView> users = userService.findAllUsers(Pageable.unpaged()).getContent();
        Assertions.assertTrue(users.contains(userModified));
        Assertions.assertEquals(userRequest.getName(), userModified.getName());
        Assertions.assertEquals(userRequest.getSurname(), userModified.getSurname());
        Assertions.assertEquals(userRequest.getUsername(), userModified.getUsername());
        Assertions.assertEquals(userRequest.getPassword(), userModified.getPassword());
    }

    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void update_NonExistentRoleGiven_ShouldThrowException() throws DataSetException {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(snapshot.size() - 1);
        UserRequest userRequest = this.fetchFromEntity(user);
        userRequest.setName("Modified Name");
        userRequest.setUsername("Modified Username");
        userRequest.setRoles(Set.of("non", "existent", "roles"));
        
        Assertions.assertThrows(Exception.class, () -> userService.update(user, userRequest));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_ExistentUserGiven_ShouldRemoveUser() throws DataSetException {
		List<User> snapshot = this.fetchSnapshot();
		User user = snapshot.get(snapshot.size() - 1);
		UserView userView = userConverter.convert(user);

		userService.delete(user.getId());
        
        List<UserView> userViews = userService.findAllUsers(Pageable.unpaged()).getContent();
        Assertions.assertFalse(userViews.contains(userView));
		Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getUser(user.getId()));
    }

    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_NonExistentUserGiven_ShouldThrowException() throws DataSetException {
    	List<User> snapshot = this.fetchSnapshot();
		User user = snapshot.get(snapshot.size() - 1);

		Assertions.assertThrows(EntityNotFoundException.class, () -> userService.delete(user.getId() + 1));
    }
}
