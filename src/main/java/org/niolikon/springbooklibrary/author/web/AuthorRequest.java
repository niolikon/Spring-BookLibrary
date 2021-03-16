package org.niolikon.springbooklibrary.author.web;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

public class AuthorRequest implements Serializable {
    
    /** Serial Version ID */
    private static final long serialVersionUID = -6152631506716661299L;

    @NotEmpty
    private String name;
    
    @NotEmpty
    private String surname;
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
