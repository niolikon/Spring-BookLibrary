package org.niolikon.springbooklibrary.book.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import org.niolikon.springbooklibrary.book.Book;
import org.niolikon.springbooklibrary.book.web.BookView;
import org.niolikon.springbooklibrary.publisher.converter.PublisherToPublisherViewConverter;
import org.niolikon.springbooklibrary.author.converter.AuthorToAuthorViewConverter;

@Component
public class BookToBookViewConverter implements Converter<Book, BookView> {

    @Autowired
    AuthorToAuthorViewConverter authorConverter;
    
    @Autowired
    PublisherToPublisherViewConverter publisherConverter;
    
    @Override
    public BookView convert(Book source) {
        BookView view = new BookView();
        view.setId(source.getId());
        view.setTitle(source.getTitle());
        view.setAuthor(authorConverter.convert(source.getAuthor()));
        view.setPublisher(publisherConverter.convert(source.getPublisher()));
        view.setQuantity(source.getQuantity());
        return view;
    }
    
}
