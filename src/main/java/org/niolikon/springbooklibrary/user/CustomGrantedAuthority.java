package org.niolikon.springbooklibrary.user;

import org.springframework.security.core.GrantedAuthority;

public class CustomGrantedAuthority implements GrantedAuthority {
    
    /** Generated serialVersionUID */ 
    private static final long serialVersionUID = 2169397640212425168L;
    
    private String name;

    public CustomGrantedAuthority(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name;
    }

}
