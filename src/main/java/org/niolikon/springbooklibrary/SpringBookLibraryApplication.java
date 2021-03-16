package org.niolikon.springbooklibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class SpringBookLibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBookLibraryApplication.class, args);
	}

}
