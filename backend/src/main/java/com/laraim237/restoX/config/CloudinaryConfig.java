package com.laraim237.restoX.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CloudinaryConfig {

	@Bean
	public Cloudinary cloudinary() {
		Dotenv dotenv = Dotenv.load();
		Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
		log.info("Cloudinary connecté : {}", cloudinary.config.cloudName);
		return cloudinary;
	}
}
