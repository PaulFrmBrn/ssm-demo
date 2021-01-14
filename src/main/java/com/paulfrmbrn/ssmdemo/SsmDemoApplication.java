package com.paulfrmbrn.ssmdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication
@EnableJdbcRepositories
public class SsmDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsmDemoApplication.class, args);
	}

}
