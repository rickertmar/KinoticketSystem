package com.dhbw.kinoticket;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@OpenAPIDefinition
@CrossOrigin(origins = {"http://localhost:3000", "https://dhbwkino.de"})
public class KinoticketApplication {

	public static void main(String[] args) {
		SpringApplication.run(KinoticketApplication.class, args);
	}

}
