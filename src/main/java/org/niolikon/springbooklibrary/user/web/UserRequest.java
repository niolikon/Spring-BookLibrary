package org.niolikon.springbooklibrary.user.web;

import java.io.Serializable;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserRequest implements Serializable {
    
    /** Serial Version ID */
    private static final long serialVersionUID = -6484202722961791765L;

    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String surname;

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;
    
    private Set<String> roles = Set.of();

}
