package com.dhbw.kinoticket;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@OpenAPIDefinition
@SpringBootApplication
public class KinoticketApplication {

	public static void main(String[] args) {
		SpringApplication.run(KinoticketApplication.class, args);
	}

}
