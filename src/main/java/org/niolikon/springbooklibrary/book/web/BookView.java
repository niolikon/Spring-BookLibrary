package org.niolikon.springbooklibrary.book.web;

import org.niolikon.springbooklibrary.author.web.AuthorView;
import org.niolikon.springbooklibrary.publisher.web.PublisherView;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BookView {
    
    private long id;
    
    private String title;
    
    private AuthorView author;
    
    private PublisherView publisher;
    
    private int quantity;
	
}
