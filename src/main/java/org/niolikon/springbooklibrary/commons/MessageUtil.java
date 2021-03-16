package org.niolikon.springbooklibrary.commons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MessageUtil {

    @Autowired
    private final MessageSource messageSource;

    public MessageUtil(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String selector, Object... parameters) {
        return messageSource.getMessage(selector, parameters, LocaleContextHolder.getLocale());
    }

    public String getMessage(String selector) {
        return messageSource.getMessage(selector, new Object[] {}, LocaleContextHolder.getLocale());
    }
}