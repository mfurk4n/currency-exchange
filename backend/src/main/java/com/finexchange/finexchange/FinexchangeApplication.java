package com.finexchange.finexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FinexchangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinexchangeApplication.class, args);
	}

}
