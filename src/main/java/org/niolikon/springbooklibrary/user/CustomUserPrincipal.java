package org.niolikon.springbooklibrary.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserPrincipal implements UserDetails {
    
    /** Generated serialVersionUID */
    private static final long serialVersionUID = -1389021648090913390L;
    
    private User user;

    public CustomUserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<CustomGrantedAuthority> result;
        
        result = user.getRoles().stream()
                .map(role -> new CustomGrantedAuthority(role.getName()))
                .collect(Collectors.toCollection(ArrayList::new));
        
        return result;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Introduce support for account expiration
        return true; // Hardcoded: checking only credentials
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Introduce support for account locking
        return true; // Hardcoded: checking only credentials
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Introduce support for credentials expiration
        return true; // Hardcoded: checking only credentials
    }

    @Override
    public boolean isEnabled() {
        // TODO Introduce support for account enabling
        return true; // Hardcoded: checking only credentials
    }

}
