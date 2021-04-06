package org.niolikon.springbooklibrary.user;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.niolikon.springbooklibrary.commons.DBUnitTest;
import org.niolikon.springbooklibrary.system.exceptions.EntityDuplicationException;
import org.niolikon.springbooklibrary.user.web.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest extends DBUnitTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    public PasswordEncoder passwordEncoder;

    private final String ADMIN_USERNAME     = "admin";
    private final String ADMIN_PASSWORD     = "admin";

    private final String USER_USERNAME      = "user";
    private final String USER_PASSWORD      = "user";
    
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
    public void get_ExistentIdGiven_AdminAuth_ShouldReturnUser() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(0);
        Long user_id = user.getId();
        List<String> user_roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        
        mockMvc.perform(MockMvcRequestBuilders.get("/users/"+ user_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.name").value(user.getName()))
        .andExpect(jsonPath("$.surname").value(user.getSurname()))
        .andExpect(jsonPath("$.username").value(user.getUsername()))
        .andExpect(jsonPath("$.password").value(user.getPassword()))
        .andExpect(jsonPath("$.roles", Matchers.hasSize(user_roles.size())))
        .andExpect(jsonPath("$.roles", Matchers.containsInAnyOrder(user_roles.toArray())));
    }
    

    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void get_OwnUserIdGiven_UserAuth_ShouldReturnUser() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(2);
        Long user_id = user.getId();
        List<String> user_roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        
        mockMvc.perform(MockMvcRequestBuilders.get("/users/"+ user_id.toString())
                .with(user(user.getUsername()))
                
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.name").value(user.getName()))
        .andExpect(jsonPath("$.surname").value(user.getSurname()))
        .andExpect(jsonPath("$.username").value(user.getUsername()))
        .andExpect(jsonPath("$.password").value(user.getPassword()))
        .andExpect(jsonPath("$.roles", Matchers.hasSize(user_roles.size())))
        .andExpect(jsonPath("$.roles", Matchers.containsInAnyOrder(user_roles.toArray())));
    }
    

    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void get_AnotherUserIdGiven_AdminAuth_ShouldReturnUser() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(0);
        Long user_id = user.getId();
        User anotheruser = snapshot.get(2);
        Long anotheruser_id = anotheruser.getId();
        List<String> anotheruser_roles = anotheruser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        
        mockMvc.perform(MockMvcRequestBuilders.get("/users/"+ anotheruser_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(anotheruser.getId()))
        .andExpect(jsonPath("$.name").value(anotheruser.getName()))
        .andExpect(jsonPath("$.surname").value(anotheruser.getSurname()))
        .andExpect(jsonPath("$.username").value(anotheruser.getUsername()))
        .andExpect(jsonPath("$.password").value(anotheruser.getPassword()))
        .andExpect(jsonPath("$.roles", Matchers.hasSize(anotheruser_roles.size())))
        .andExpect(jsonPath("$.roles", Matchers.containsInAnyOrder(anotheruser_roles.toArray())));
    }
    

    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void get_AnotherUserIdGiven_UserAuth_ShouldReturnError() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(1);
        Long user_id = user.getId();
        User anotheruser = snapshot.get(2);
        Long anotheruser_id = anotheruser.getId();
        
        mockMvc.perform(MockMvcRequestBuilders.get("/users/"+ anotheruser_id.toString())
                .with(user(user.getUsername()))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNotAcceptable());
    }
    

    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void get_ExistentIdGiven_NoAuth_ShouldReturnError() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(0);
        Long user_id = user.getId();
        
        mockMvc.perform(MockMvcRequestBuilders.get("/users/"+ user_id.toString())
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void get_NonExistentIdGiven_AdminAuth_ShouldReturnError() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(snapshot.size() - 1);
        Long user_outofboundId = user.getId() + 1;

        mockMvc.perform(MockMvcRequestBuilders.get("/users/"+ user_outofboundId.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.exception").value(EntityNotFoundException.class.getSimpleName()));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getAll_AdminAuth_ShouldReturnAll() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        Integer snapshot_lastIdx = snapshot.size() - 1;
        User user_first = snapshot.get(0);
        List<String> user_first_roles = user_first.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        User user_last = snapshot.get(snapshot_lastIdx);
        List<String> user_last_roles = user_last.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(user_first.getId().toString()))
        .andExpect(jsonPath("$.content[0].name").value(user_first.getName()))
        .andExpect(jsonPath("$.content[0].surname").value(user_first.getSurname()))
        .andExpect(jsonPath("$.content[0].username").value(user_first.getUsername()))
        .andExpect(jsonPath("$.content[0].password").value(user_first.getPassword()))
        .andExpect(jsonPath("$.content[0].roles", Matchers.hasSize(user_first_roles.size())))
        .andExpect(jsonPath("$.content[0].roles", Matchers.containsInAnyOrder(user_first_roles.toArray())))
        .andExpect(jsonPath("$.content["+snapshot_lastIdx+"].id").value(user_last.getId().toString()))
        .andExpect(jsonPath("$.content["+snapshot_lastIdx+"].name").value(user_last.getName()))
        .andExpect(jsonPath("$.content["+snapshot_lastIdx+"].surname").value(user_last.getSurname()))
        .andExpect(jsonPath("$.content["+snapshot_lastIdx+"].username").value(user_last.getUsername()))
        .andExpect(jsonPath("$.content["+snapshot_lastIdx+"].password").value(user_last.getPassword()))
        .andExpect(jsonPath("$.content["+snapshot_lastIdx+"].roles", Matchers.hasSize(user_last_roles.size())))
        .andExpect(jsonPath("$.content["+snapshot_lastIdx+"].roles", Matchers.containsInAnyOrder(user_last_roles.toArray())));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getAll_UserAuth_ShouldReturnAll() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        Integer snapshot_lastIdx = snapshot.size() - 1;
        User user_first = snapshot.get(0);
        List<String> user_first_roles = user_first.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        User user_last = snapshot.get(snapshot_lastIdx);
        List<String> user_last_roles = user_last.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(user_first.getId().toString()))
        .andExpect(jsonPath("$.content[0].name").value(user_first.getName()))
        .andExpect(jsonPath("$.content[0].surname").value(user_first.getSurname()))
        .andExpect(jsonPath("$.content[0].username").value(user_first.getUsername()))
        .andExpect(jsonPath("$.content[0].password").value(user_first.getPassword()))
        .andExpect(jsonPath("$.content[0].roles", Matchers.hasSize(user_first_roles.size())))
        .andExpect(jsonPath("$.content[0].roles", Matchers.containsInAnyOrder(user_first_roles.toArray())))
        .andExpect(jsonPath("$.content["+snapshot_lastIdx+"].id").value(user_last.getId().toString()))
        .andExpect(jsonPath("$.content["+snapshot_lastIdx+"].name").value(user_last.getName()))
        .andExpect(jsonPath("$.content["+snapshot_lastIdx+"].surname").value(user_last.getSurname()))
        .andExpect(jsonPath("$.content["+snapshot_lastIdx+"].username").value(user_last.getUsername()))
        .andExpect(jsonPath("$.content["+snapshot_lastIdx+"].password").value(user_last.getPassword()))
        .andExpect(jsonPath("$.content["+snapshot_lastIdx+"].roles", Matchers.hasSize(user_last_roles.size())))
        .andExpect(jsonPath("$.content["+snapshot_lastIdx+"].roles", Matchers.containsInAnyOrder(user_last_roles.toArray())));
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void getAll_NoAuth_ShouldReturnError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void post_NewUserGiven_AdminAuth_ShouldCreateUser() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        Integer snapshot_lastIdx = snapshot.size() - 1;
        User user_first = snapshot.get(0);
        User user_last = snapshot.get(snapshot_lastIdx);
        UserRequest userRequest = this.fetchFromEntity(user_first);
        userRequest.setName("Test user name");
        userRequest.setSurname("Test user surname");
        userRequest.setUsername("test");
        userRequest.setPassword("test");
        
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        .andDo(print())
        
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.name").value(userRequest.getName()))
        .andExpect(jsonPath("$.surname").value(userRequest.getSurname()))
        .andExpect(jsonPath("$.username").value(userRequest.getUsername()))
        .andExpect(jsonPath("$.password").value(JsonPasswordMatcher.of(userRequest.getPassword(), passwordEncoder)))
        
        .andExpect(jsonPath("$.roles", Matchers.hasSize(userRequest.getRoles().size())))
        .andExpect(jsonPath("$.roles", Matchers.containsInAnyOrder(userRequest.getRoles().toArray())));
    }

    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void post_NewUserGiven_UserAuth_ShouldReturnError() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        Integer snapshot_lastIdx = snapshot.size() - 1;
        User user_first = snapshot.get(0);
        User user_last = snapshot.get(snapshot_lastIdx);
        UserRequest userRequest = this.fetchFromEntity(user_first);
        userRequest.setName("Test user name");
        userRequest.setSurname("Test user surname");
        userRequest.setUsername("test");
        userRequest.setPassword("test");
        
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_USERNAME, USER_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isForbidden());
    }

    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void post_NewUserGiven_NoAuth_ShouldReturnError() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        Integer snapshot_lastIdx = snapshot.size() - 1;
        User user_first = snapshot.get(0);
        User user_last = snapshot.get(snapshot_lastIdx);
        UserRequest userRequest = this.fetchFromEntity(user_first);
        userRequest.setName("Test user name");
        userRequest.setSurname("Test user surname");
        userRequest.setUsername("test");
        userRequest.setPassword("test");
        
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void post_NewUserWithNoRolesGiven_AdminAuth_ShouldReturnError() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        Integer snapshot_lastIdx = snapshot.size() - 1;
        User user_first = snapshot.get(0);
        User user_last = snapshot.get(snapshot_lastIdx);
        UserRequest userRequest = this.fetchFromEntity(user_last);
        userRequest.setName("Test user name");
        userRequest.setSurname("Test user surname");
        userRequest.setUsername("test");
        userRequest.setPassword("test");
        userRequest.setRoles(Set.of());
        
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRequest))

                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isUnprocessableEntity());
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void post_ExistentUserGiven_AdminAuth_ShouldReturnError() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(0);
        UserRequest userRequest = new UserRequest();
        userRequest.setName(user.getName());
        userRequest.setSurname(user.getSurname());
        userRequest.setUsername(user.getUsername());
        userRequest.setPassword(user.getPassword());
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        userRequest.setRoles(roles);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.exception").value(EntityDuplicationException.class.getSimpleName()));
    }

    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_ExistentIdGiven_AdminAuth_ShouldDeleteUser() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(0);
        Long user_id = user.getId();
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/"+ user_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNoContent());
    }

    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_ExistentIdGiven_UserAuth_ShouldReturnError() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(0);
        Long user_id = user.getId();
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/"+ user_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(USER_USERNAME, USER_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isForbidden());
    }

    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_ExistentIdGiven_NoAuth_ShouldReturnError() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(0);
        Long user_id = user.getId();
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/"+ user_id.toString())
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isUnauthorized());
    }

    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void delete_NonExistentIdGiven_AdminAuth_ShouldReturnError() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(snapshot.size() - 1);
        Long user_outofboundId = user.getId() + 1;

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/"+ user_outofboundId.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.exception").value(EntityNotFoundException.class.getSimpleName()));
    }


    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_ExistentUserGiven_AdminAuth_ShouldModifyUser() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(0);
        Long user_id = user.getId();
        UserRequest userRequest = this.fetchFromEntity(user);
        userRequest.setName("Modified name");
        
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + user_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user_id))
        .andExpect(jsonPath("$.name").value(userRequest.getName()))
        .andExpect(jsonPath("$.surname").value(userRequest.getSurname()))
        .andExpect(jsonPath("$.username").value(userRequest.getUsername()))
        .andExpect(jsonPath("$.password").value(JsonPasswordMatcher.of(userRequest.getPassword(), passwordEncoder)))
        //.andExpect(jsonPath("$.password").value(passwordEncoder.encode(userRequest.getPassword())))
        .andExpect(jsonPath("$.roles", Matchers.hasSize(userRequest.getRoles().size())))
        .andExpect(jsonPath("$.roles", Matchers.containsInAnyOrder(userRequest.getRoles().toArray())));
    }


    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_OwnUserIdGiven_UserAuth_ShouldModifyUser() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(1);
        Long user_id = user.getId();
        UserRequest userRequest = this.fetchFromEntity(user);
        userRequest.setName("Modified name");
        
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + user_id.toString())
                .with(user(user.getUsername()))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user_id))
        .andExpect(jsonPath("$.name").value(userRequest.getName()))
        .andExpect(jsonPath("$.surname").value(userRequest.getSurname()))
        .andExpect(jsonPath("$.username").value(userRequest.getUsername()))
        .andExpect(jsonPath("$.password").value(JsonPasswordMatcher.of(userRequest.getPassword(), passwordEncoder)))
        //.andExpect(jsonPath("$.password").value(passwordEncoder.encode(userRequest.getPassword())))
        .andExpect(jsonPath("$.roles", Matchers.hasSize(userRequest.getRoles().size())))
        .andExpect(jsonPath("$.roles", Matchers.containsInAnyOrder(userRequest.getRoles().toArray())));
    }


    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_OwnUserIdModifiedUsernameGiven_UserAuth_ShouldReturnError() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(1);
        Long user_id = user.getId();
        UserRequest userRequest = this.fetchFromEntity(user);
        userRequest.setName("Modified name");
        userRequest.setUsername("Modified username");
        
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + user_id.toString())
                .with(user(user.getUsername()))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNotAcceptable());
    }


    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_OwnUserIdModifiedRolesGiven_UserAuth_ShouldReturnError() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(1);
        Long user_id = user.getId();
        UserRequest userRequest = this.fetchFromEntity(user);
        userRequest.setName("Modified name");
        userRequest.getRoles().add("role1");
        
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + user_id.toString())
                .with(user(user.getUsername()))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNotAcceptable());
    }


    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_AnotherUserIdGiven_AdminAuth_ShouldModifyUser() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(1);
        Long user_id = user.getId();
        User anotheruser = snapshot.get(2);
        Long anotheruser_id = anotheruser.getId();
        UserRequest userRequest = this.fetchFromEntity(anotheruser);
        userRequest.setName("Modified name");
        userRequest.setPassword("Modified password");
        
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + anotheruser_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(anotheruser_id))
        .andExpect(jsonPath("$.name").value(userRequest.getName()))
        .andExpect(jsonPath("$.surname").value(userRequest.getSurname()))
        .andExpect(jsonPath("$.username").value(userRequest.getUsername()))
        .andExpect(jsonPath("$.password").value(JsonPasswordMatcher.of(userRequest.getPassword(), passwordEncoder)))
        //.andExpect(jsonPath("$.password").value(passwordEncoder.encode(userRequest.getPassword())))
        .andExpect(jsonPath("$.roles", Matchers.hasSize(userRequest.getRoles().size())))
        .andExpect(jsonPath("$.roles", Matchers.containsInAnyOrder(userRequest.getRoles().toArray())));
    }


    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_AnotherUserIdGiven_UserAuth_ShouldReturnError() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(1);
        Long user_id = user.getId();
        User anotheruser = snapshot.get(2);
        Long anotheruser_id = anotheruser.getId();
        UserRequest userRequest = this.fetchFromEntity(anotheruser);
        userRequest.setName("Modified name");
        
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + anotheruser_id.toString())
                .with(user(user.getUsername()))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNotAcceptable());
    }


    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_ExistentUserGiven_NoAuth_ShouldReturnError() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(0);
        Long user_id = user.getId();
        UserRequest userRequest = this.fetchFromEntity(user);
        userRequest.setName("Modified name");
        
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + user_id.toString())
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_NonExistentUserGiven_AdminAuth_ShouldReturnError() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(snapshot.size() - 1);
        Long user_outofboundId = user.getId() + 1L;
        UserRequest userRequest = new UserRequest();
        userRequest.setName(user.getName());
        
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + user_outofboundId.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.exception").value(EntityNotFoundException.class.getSimpleName()));
    }


    @Test
    @DatabaseSetup(value = "/dataset-users.xml", type = DatabaseOperation.CLEAN_INSERT)
    public void put_ExistentUserWithNoRolesGiven_AdminAuth_ShouldReturnError() throws Exception {
        List<User> snapshot = this.fetchSnapshot();
        User user = snapshot.get(0);
        Long user_id = user.getId();
        UserRequest userRequest = this.fetchFromEntity(user);
        userRequest.setRoles(Set.of());
        
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + user_id.toString())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRequest))
                .accept(MediaType.APPLICATION_JSON))
        
        //.andDo(print())
        
        .andExpect(status().isUnprocessableEntity());
    }
    
    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    

    public static class JsonPasswordMatcher extends org.hamcrest.BaseMatcher<String> {
        
        private String rawPassword;
        
        private PasswordEncoder passwordEncoder;
        
        private JsonPasswordMatcher(String rawPassword, PasswordEncoder passwordEncoder) {
            this.rawPassword = rawPassword;
            this.passwordEncoder = passwordEncoder;
        }
        
        public static JsonPasswordMatcher of(String rawPassword, PasswordEncoder passwordEncoder) {
            return new JsonPasswordMatcher(rawPassword, passwordEncoder);
        }

        @Override
        public boolean matches(Object actual) {
            
            return passwordEncoder.matches(rawPassword, (String) actual);
        }

        @Override
        public void describeTo(Description description) {
            // TODO Auto-generated method stub
        }
        
    }
}
