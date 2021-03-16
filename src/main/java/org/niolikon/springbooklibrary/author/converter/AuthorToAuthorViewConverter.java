package org.niolikon.springbooklibrary.author.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import org.niolikon.springbooklibrary.author.Author;
import org.niolikon.springbooklibrary.author.web.AuthorView;

@Component
public class AuthorToAuthorViewConverter  implements Converter<Author, AuthorView> {

    @Override
    public AuthorView convert(@NonNull Author source) {
        AuthorView view = new AuthorView();
        view.setId(source.getId());
        view.setName(source.getName());
        view.setSurname(source.getSurname());
        return view;
    }

}
