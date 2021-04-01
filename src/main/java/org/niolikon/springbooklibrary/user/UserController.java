package org.niolikon.springbooklibrary.user;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.niolikon.springbooklibrary.user.web.UserRequest;
import org.niolikon.springbooklibrary.user.web.UserView;

@RestController
@RequestMapping("/users")
@Api(tags="Management of User entities")
public class UserController {
    
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    @ApiOperation(
            value = "Read user by ID", notes = "Returns User data in JSON", response = UserView.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The User has been fetched"),
            @ApiResponse(code = 404, message = "Could not find the specified User"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public UserView getUser(@ApiParam("The ID of the User") @PathVariable Long id) {
        return service.getUser(id);
    }

    @GetMapping
    @ResponseBody
    @ApiOperation(
            value = "Read all users", notes = "Returns User data in JSON", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The Users have been fetched"),
            @ApiResponse(code = 404, message = "No Users are present in the repository"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public Page<UserView> getAllUsers(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.findAllUsers(pageable);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @ApiOperation(
            value = "Create a user", notes = "Stores the input JSON User data", response = UserView.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The User has been stored"),
            @ApiResponse(code = 409, message = "Could not complete the storage, the input User data would cause duplication"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public UserView create(@ApiParam("The input User data") @RequestBody @Valid UserRequest req) {
        return service.create(req);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(
            value = "Delete a user", notes = "Deletes the specified User data", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "The User has been deleted"),
            @ApiResponse(code = 404, message = "Could not find the specified User"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public void deleteUser(@ApiParam("The ID of the User") @PathVariable Long id) {
        service.delete(id);
    }
    
    @PutMapping("/{id}")
    @ApiOperation(
            value = "Update a user", notes = "Modifies the specified User with the input JSON User data", response = UserView.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The User has been modified"),
            @ApiResponse(code = 404, message = "Could not find the specified User"),
            @ApiResponse(code = 409, message = "Could not complete the modification, the input User data would cause duplication"),
            @ApiResponse(code = 403, message = "You are not authorized to access this resource"),
            @ApiResponse(code = 401, message = "You are not logged in") })
    public UserView updateUser(@ApiParam("The ID of the User") @PathVariable Long id,
            @ApiParam("The input User data") @RequestBody @Valid UserRequest req) {
        User author = service.findUserOrThrow(id);
        return service.update(author, req);
    }

}
