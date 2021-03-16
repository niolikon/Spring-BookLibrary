package org.niolikon.springbooklibrary.book.web;

import org.niolikon.springbooklibrary.author.web.AuthorView;
import org.niolikon.springbooklibrary.publisher.web.PublisherView;

public class BookView {
    
    private long id;
    
    private String title;
    
    private AuthorView author;
    
    private PublisherView publisher;
    
    private int quantity;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AuthorView getAuthor() {
        return author;
    }

    public void setAuthor(AuthorView author) {
        this.author = author;
    }

    public PublisherView getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherView publisher) {
        this.publisher = publisher;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
