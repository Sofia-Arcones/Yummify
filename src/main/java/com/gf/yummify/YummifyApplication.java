package com.gf.yummify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.gf.yummify")
public class YummifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(YummifyApplication.class, args);
	}

}
