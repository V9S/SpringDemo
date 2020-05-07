package com.lmz.qualifier.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lmz.qualifier.service.InterfacePrint;

/**
 * @author ZLM
 *
 */
@RestController
public class Controller {

	@Autowired
	@Qualifier(value = "1")
	private InterfacePrint i;

	@RequestMapping("/print")
	private String print() {
		return random();
	}

	private String random() {

		return String.valueOf(Math.round(Math.random() * 10) % 2);

	}
}
