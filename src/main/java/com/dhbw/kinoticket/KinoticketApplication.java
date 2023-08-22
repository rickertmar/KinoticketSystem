package com.dhbw.kinoticket;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class KinoticketApplication {

	public static void main(String[] args) {
		SpringApplication.run(KinoticketApplication.class, args);
	}

}
