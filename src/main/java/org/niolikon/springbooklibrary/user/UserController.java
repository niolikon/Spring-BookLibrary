package org.niolikon.springbooklibrary.user;

import javax.validation.Valid;

import org.niolikon.springbooklibrary.user.web.UserRequest;
import org.niolikon.springbooklibrary.user.web.UserView;
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

@RestController
@RequestMapping("/users")
public class UserController {
    
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public UserView getUser(@PathVariable Long id) {
        return service.getUser(id);
    }

    @GetMapping
    @ResponseBody
    public Page<UserView> getAllUsers(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.findAllUsers(pageable);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserView create(@RequestBody @Valid UserRequest req) {
        return service.create(req);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        service.delete(id);
    }
    
    @PutMapping("/{id}")
    public UserView updateUser(@PathVariable Long id,
            @RequestBody @Valid UserRequest req) {
        User author = service.findUserOrThrow(id);
        return service.update(author, req);
    }

}
