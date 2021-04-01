package org.niolikon.springbooklibrary.author.web;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AuthorRequest implements Serializable {
    
    /** Serial Version ID */
    private static final long serialVersionUID = -6152631506716661299L;

    @NotEmpty
    private String name;
    
    @NotEmpty
    private String surname;
    
}
