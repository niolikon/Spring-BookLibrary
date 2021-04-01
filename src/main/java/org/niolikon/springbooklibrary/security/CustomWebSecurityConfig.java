package org.niolikon.springbooklibrary.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class CustomWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("localUserDetailsService")
    UserDetailsService localUserDetailsService;
    
    private static final String[] SWAGGER_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            
            .authorizeRequests()
            
            .antMatchers(HttpMethod.GET, "/authors/**").hasAuthority("ROLE_USER")
            .antMatchers("/authors/**").hasAuthority("ROLE_ADMIN")
            .antMatchers(HttpMethod.GET, "/authors").hasAuthority("ROLE_USER")
            .antMatchers("/authors").hasAuthority("ROLE_ADMIN")

            .antMatchers(HttpMethod.GET, "/books/**").hasAuthority("ROLE_USER")
            .antMatchers("/books/**").hasAuthority("ROLE_ADMIN")
            .antMatchers(HttpMethod.GET, "/books").hasAuthority("ROLE_USER")
            .antMatchers("/books").hasAuthority("ROLE_ADMIN")

            .antMatchers(HttpMethod.GET, "/publisher/**").hasAuthority("ROLE_USER")
            .antMatchers("/publisher/**").hasAuthority("ROLE_ADMIN")
            .antMatchers(HttpMethod.GET, "/publisher").hasAuthority("ROLE_USER")
            .antMatchers("/publisher").hasAuthority("ROLE_ADMIN")

            .antMatchers(HttpMethod.GET, "/users/**").hasAuthority("ROLE_USER")
            .antMatchers(HttpMethod.PUT, "/users/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .antMatchers("/users/**").hasAuthority("ROLE_ADMIN")
            .antMatchers(HttpMethod.GET, "/users").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .antMatchers(HttpMethod.PUT, "/users").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .antMatchers("/users").hasAuthority("ROLE_ADMIN")
            
            .anyRequest().authenticated()
            .and()
            .httpBasic().realmName(CustomAuthEntryPoint.REALM).authenticationEntryPoint(authEntryPoint())
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
    }
    
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers(SWAGGER_WHITELIST)

            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers(HttpMethod.GET, "/csrf")
            .antMatchers(HttpMethod.GET, "/error")
            .antMatchers(HttpMethod.GET, "/h2-console/**")
            .antMatchers(HttpMethod.POST, "/h2-console/**");
    }
    
    
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsService())
            .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean 
    public UserDetailsService userDetailsService() {
        return localUserDetailsService;
    }
    
    @Bean
    public AuthenticationEntryPoint authEntryPoint() {
        return new CustomAuthEntryPoint();
    }
    
}
