package org.niolikon.springbooklibrary.publisher.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import org.niolikon.springbooklibrary.publisher.Publisher;
import org.niolikon.springbooklibrary.publisher.web.PublisherView;

@Component
public class PublisherToPublisherViewConverter implements Converter<Publisher, PublisherView>{

    @Override
    public PublisherView convert(@NonNull Publisher source) {
        PublisherView view = new PublisherView();
        view.setId(source.getId());
        view.setName(source.getName());
        return view;
    }
    
}
