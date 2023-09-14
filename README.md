# KinoticketSystem

## Overview

This is the backend repository for the KinoticketSystem project by DHBW Mannheim Students - WWI SEA 2022. It's a java maven build of a spring boot application with REST API. Here are the key technologies and tools used:

* Programming language:
  * Java
* Build and Dependency Management:
  * **Maven**: Build automation and dependency management tool; simplifies project configuration, manages dependencies, compiles code, and generates artifacts like JAR files
* Java Libaries and Frameworks:
  * **Lombok**: Simplifies the creation of boilerplate code, such as getters, setters, and constructors, using annotations, reducing code verbosity
  * **Spring Boot**: Building Java applications with minimal configuration, simplifying the development of production-ready, stand-alone applications
  * **Spring Doc**: Toolset for generating API documentation for Spring-based applications, making it easier to document RESTful APIs
  * **Spring Data JPA**: Simplifies data access using the Java Persistence API (JPA) and provides a consistent and efficient way to work with databases
  * **Hibernate**: Java-based Object-Relational Mapping (ORM) framework that simplifies database interactions by mapping Java objects to database tables
* Database:
  * **MySQL**
* Authentication and Authorization:
  * **JWT (JSON Web Tokens)**: JWT is a compact, self-contained way to represent and transmit information between parties as a JSON object. It is (commonly) used for implementing authentication and authorization in web applications
* Testing Frameworks:
  * **JUnit**: Testing framework for automating unit tests, ensuring the reliability and correctness of individual code units within applications
  * **Mockito**: Java mocking framework used for creating mock objects to isolate and test specific parts of code, especially in unit testing scenarios
  * Combination of JUnit and Mockito to test api endpoints in isolation with mock objects and without building/running the app everytime to send requests or get responses

## Project Structure

- **`config/`**: Configuration files, filters and security specific to the project.

- **`controller/`**: REST controller to handle http requests.

- **`entity/`**: Base entities.

- **`repository/`**: Interfaces for DB communication.

- **`request/`**: Request entities.

- **`response/`**: Response entities.

- **`service/`**: Business logic and functionalities.

- **`test/`**: Unit tests for services and controller. 

## How to Run

To run the project locally:

1. Clone the repository.
2. Navigate to the application class file in **`com.dhbw.kinoticket/`**.
3. Run `KinoticketApplication.class`.
