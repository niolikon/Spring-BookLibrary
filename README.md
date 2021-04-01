## Spring-BookLibrary
> Spring RESTFul BookLibrary (Case Study)

A small but powerful RESTFul service for the management of a Book Library.


## Motivation
This project is aimed on enforcing the knowledge of Spring Framework, REST services
and related technologies with emphasis on testability.


## Technologies
Project is created with:
* Java 11
* Maven
* PostgreSQL
* Spring Boot 2.3.9
* Spring Web
* Spring Data
* DBUnit 2.5.4
* Spring Test DBUnit 1.3.0
* Jackson 2.11.1
* Swagger 2.9.2
* Lombok 1.18.18


## Features
- [x] Book management
- [x] User management and authentication
- [ ] User authorization
- [ ] Reservation management


## Setup
Spring-BookLibrary is a [Spring Boot](https://spring.io/guides/gs/spring-boot) application built using [Maven](https://spring.io/guides/gs/maven/). You can build a jar file and run it from the command line:


```
git clone https://github.com/niolikon/Spring-BookLibrary.git
cd Spring-BookLibrary
./mvnw package
java -jar target/*.jar
```

## Documentation
The exported RESTFul APIs are documented using [Swagger framework](https://swagger.io/), you can access the provided documentation by running Spring-BookLibrary and opening [Swing UI](http://localhost:8080/springbooklibrary/swagger-ui.html) on your browser.

<img src="Spring-BootLibrary_Swagger-Capture.jpg">

## Test
This project provides a [sample dashboard](Spring-BootLibrary_Insomnia-Dashboard.json) for [Insomnia REST Client](https://insomnia.rest/)
with some pre-configured REST requests.

<img src="Spring-BootLibrary_Insomnia-Capture.jpg">

# License

The Spring-BookLibrary (Case Study) application is released under [MIT License](LICENSE).
