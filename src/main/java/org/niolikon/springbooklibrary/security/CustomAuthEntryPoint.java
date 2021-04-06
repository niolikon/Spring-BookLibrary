package org.niolikon.springbooklibrary.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.niolikon.springbooklibrary.system.MessageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import lombok.extern.java.Log;

@Log
public class CustomAuthEntryPoint extends BasicAuthenticationEntryPoint {
    
    public static String REALM = "Spring-BookLibrary";
    
    @Autowired
    private MessageProvider messageProvider;

    @Override
    public void afterPropertiesSet() {
        setRealmName(REALM);
        super.afterPropertiesSet();
    }

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        
        String errorMsg = messageProvider.getMessage("authentication.Error");
        log.warning(errorMsg);
        log.warning(request.getRequestURL() + " " + request.getQueryString());
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");
        
        response.getWriter().println(errorMsg);
    }
    
}
