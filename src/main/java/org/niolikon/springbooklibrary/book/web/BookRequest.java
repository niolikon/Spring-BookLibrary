package org.niolikon.springbooklibrary.book.web;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BookRequest implements Serializable {

    /** Serial Version ID */
    private static final long serialVersionUID = -6428161225442573088L;

    @NotEmpty
    private String title;

    @NotNull
    private Long authorId;
    
    @NotNull
    private Long publisherId;
    
    private int quantity;
    
}
