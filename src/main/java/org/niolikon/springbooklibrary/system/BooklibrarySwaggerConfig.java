package org.niolikon.springbooklibrary.system;

import static springfox.documentation.builders.PathSelectors.regex;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Lists;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class BooklibrarySwaggerConfig {

    // URL Swagger UI: http://localhost:8080/springbooklibrary/swagger-ui.html
    // URL Swagger: http://localhost:8080/springbooklibrary/v2/api-docs
    // Docs: https://swagger.io/docs/
    
    @Bean
    public Docket api()
    {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                    .apis(RequestHandlerSelectors.basePackage("org.niolikon.springbooklibrary"))
                    .paths(regex("/.*"))
                .build()
                .apiInfo(apiInfo())
                .securityContexts(Lists.newArrayList(securityContext()))
                .securitySchemes(Lists.newArrayList(basicAuthScheme()));
    }
    
    private ApiInfo apiInfo() 
    {
        return new ApiInfoBuilder()
                .title("Spring-BookLibrary")
                .description("A small but powerful RESTFul service for the management of a Book Library")
                .version("0.0.1-A04")
                .license("MIT License")
                .licenseUrl("https://github.com/niolikon/Spring-BookLibrary/blob/main/LICENSE")
                .contact(new Contact("Simone Andrea Muscas",  "https://www.linkedin.com/in/simoneandreamuscas/", 
                        "simoneandrea.muscas@gmail.com"))
                .build();
    }
    
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(Arrays.asList(basicAuthReference()))
                .forPaths(regex("/.*"))
                .build();
    }
    
    private SecurityScheme basicAuthScheme() {
        return new BasicAuth("basicAuth");   
    }

    private SecurityReference basicAuthReference() {
        return new SecurityReference("basicAuth", new AuthorizationScope[0]);
    }

}
