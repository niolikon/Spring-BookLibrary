package org.niolikon.springbooklibrary.publisher.web;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

public class PublisherRequest implements Serializable {
    
    /** Serial Version ID */
    private static final long serialVersionUID = -4023001739185863141L;
    
    @NotEmpty
    private String name;
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
