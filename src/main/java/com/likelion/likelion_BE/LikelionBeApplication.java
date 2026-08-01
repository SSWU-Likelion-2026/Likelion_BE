package com.likelion.likelion_BE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LikelionBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LikelionBeApplication.class, args);
	}

}
