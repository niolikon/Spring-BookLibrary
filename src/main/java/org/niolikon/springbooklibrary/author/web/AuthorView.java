package org.niolikon.springbooklibrary.author.web;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AuthorView {
    
    private long id;

    private String name;

    private String surname;
    
}
