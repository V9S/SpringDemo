package com.lmz.qualifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author ZLM
 *
 */
//@RestController
@SpringBootApplication
public class Appliction {

	public static void main(String[] args) {
		SpringApplication.run(Appliction.class, args);
	}
//	@RequestMapping("/hello")
//	public String hello() {
//		return "Hello";
//	}
}
