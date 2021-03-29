package org.niolikon.springbooklibrary.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.niolikon.springbooklibrary.user.converter.UserToUserViewConverter;
import org.niolikon.springbooklibrary.user.web.UserRequest;
import org.niolikon.springbooklibrary.user.web.UserView;
import org.niolikon.springbooklibrary.system.MessageProvider;
import org.niolikon.springbooklibrary.system.exceptions.EntityDuplicationException;
import org.niolikon.springbooklibrary.system.exceptions.EntityNotFoundException;
import org.niolikon.springbooklibrary.system.exceptions.EntityNotProcessableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final UserToUserViewConverter userConverter;
    private final MessageProvider messageProvider;
    
    public UserService(UserRepository userRepo,
            RoleRepository roleRepo,
            UserToUserViewConverter userConverter,
            MessageProvider messageUtil) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.userConverter = userConverter;
        this.messageProvider = messageUtil;
    }
    
    public User findUserOrThrow(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(messageProvider.getMessage("user.NotFound", id)));
    }
    
    public Role findRoleOrThrow(String name) {
        return roleRepo.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(messageProvider.getMessage("role.NotFound", name)));
    }
    
    public UserView getUser(Long id) {
        User user = findUserOrThrow(id);
        return userConverter.convert(user);
    }

    public Page<UserView> findAllUsers(Pageable pageable) {
        Page<User> users = userRepo.findAll(pageable);
        List<UserView> userViews = new ArrayList<>();
        users.forEach(user -> {
            UserView userView = userConverter.convert(user);
            userViews.add(userView);
        });
        return new PageImpl<>(userViews, pageable, users.getTotalElements());
    }
    
    public UserView create(UserRequest req) {
        User user = new User();
        this.fetchFromRequest(user, req);
        
        if (user.getRoles().isEmpty()) {
            throw new EntityNotProcessableException(messageProvider.getMessage("user.MustHaveRoles"));
        }

        try {
            User userSaved = userRepo.save(user);
            return userConverter.convert(userSaved);
        } catch (DataIntegrityViolationException e) {
            throw new EntityDuplicationException(messageProvider.getMessage("user.Duplication", user.getName(), user.getSurname()));
        }
    }
    
    @Transactional
    public void delete(Long id) {
        try {
            userRepo.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(messageProvider.getMessage("user.NotFound", id));
        }
    }
    
    public UserView update(User user, UserRequest req) {
        User userUpdated = this.fetchFromRequest(user, req);
        
        if (userUpdated.getRoles().isEmpty()) {
            throw new EntityNotProcessableException(messageProvider.getMessage("user.MustHaveRoles"));
        }
        User userSaved = userRepo.save(userUpdated);
        return userConverter.convert(userSaved);
    }
    
    private User fetchFromRequest(User user, UserRequest req) {
        user.setName(req.getName());
        user.setSurname(req.getSurname());
        user.setUsername(req.getUsername());
        user.setPassword(req.getPassword());
        
        Set<Role> roles = req.getRoles().stream()
                .map( name -> this.findRoleOrThrow(name))
                .collect(Collectors.toSet());
        user.setRoles(roles);
        
        return user;
    }

}
