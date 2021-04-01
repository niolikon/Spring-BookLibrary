package org.niolikon.springbooklibrary.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

import org.niolikon.springbooklibrary.user.User;
import org.niolikon.springbooklibrary.user.UserRepository;

@Service("localUserDetailsService")
@Log
public class LocalUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("Requested userdetails for username: " + username);
        
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        
        String[] authorities = user.getRoles().stream()
                            .map( role -> "ROLE_" + role.getName().toUpperCase())
                            .toArray(String[]::new);
        
        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(username);
        builder.disabled(false);
        builder.password(user.getPassword());
        builder.authorities(authorities);
        
        return builder.build();
    }
    
}
