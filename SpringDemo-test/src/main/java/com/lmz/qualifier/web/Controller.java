package com.lmz.qualifier.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lmz.qualifier.service.InterfacePrint;

/**
 * @author ZLM
 *当注入的service有多个实现类的时候，可以有@Qualifier注解指定具体实现类，value是实现类注册bean的名字
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class Controller {

	@Autowired
	@Qualifier(value = "bean1")
	private InterfacePrint i;
	
	@RequestMapping("/print")
	private String print() {
	    System.out.println("进入print");
	    return "1";
//		return random();
	}

	private String random() {

		return String.valueOf(Math.round(Math.random() * 10) % 2);

	}
}
