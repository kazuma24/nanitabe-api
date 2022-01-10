package com.fact.nanitabe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NanitabeApplication {

	public static void main(String[] args) {
		SpringApplication.run(NanitabeApplication.class, args);
	}

}
