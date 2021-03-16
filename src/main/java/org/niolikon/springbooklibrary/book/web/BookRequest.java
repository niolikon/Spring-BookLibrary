package org.niolikon.springbooklibrary.book.web;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
    
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
