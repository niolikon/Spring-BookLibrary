package org.niolikon.springbooklibrary.system;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

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
                .apiInfo(apiInfo());
    }
    
    private ApiInfo apiInfo() 
    {
        return new ApiInfoBuilder()
                .title("Spring-BookLibrary")
                .description("A small but powerful RESTFul service for the management of a Book Library")
                .version("0.0.1-A03")
                .license("MIT License")
                .licenseUrl("https://github.com/niolikon/Spring-BookLibrary/blob/main/LICENSE")
                .contact(new Contact("Simone Andrea Muscas",  "https://www.linkedin.com/in/simoneandreamuscas/", 
                        "simoneandrea.muscas@gmail.com"))
                .build();
    }

}