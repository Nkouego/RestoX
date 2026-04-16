package com.laraim237.restoX;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RestoXApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestoXApplication.class, args);
	}

}
