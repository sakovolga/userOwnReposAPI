package com.example.userOwnReposAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UserOwnReposApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserOwnReposApiApplication.class, args);
	}

}
