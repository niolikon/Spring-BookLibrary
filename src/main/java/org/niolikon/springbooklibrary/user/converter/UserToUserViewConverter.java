package org.niolikon.springbooklibrary.user.converter;

import java.util.Set;
import java.util.stream.Collectors;

import org.niolikon.springbooklibrary.user.Role;
import org.niolikon.springbooklibrary.user.User;
import org.niolikon.springbooklibrary.user.web.UserView;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class UserToUserViewConverter  implements Converter<User, UserView> {

    @Override
    public UserView convert(@NonNull User source) {
        UserView view = new UserView();
        view.setId(source.getId());
        view.setName(source.getName());
        view.setSurname(source.getSurname());
        view.setUsername(source.getUsername());
        view.setPassword(source.getPassword());
        
        Set<String> roles = source.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        view.setRoles(roles);
        
        return view;
    }
    
}
