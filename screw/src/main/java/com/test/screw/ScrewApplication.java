package com.test.screw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ScrewApplication {
	public static void main(String[] args) {
		SpringApplication.run(ScrewApplication.class, args);
		ScrewApplicationTests a = new ScrewApplicationTests();
		a.contextLoads();
	}

}
