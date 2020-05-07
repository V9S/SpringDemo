package com.lmz.qualifier.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lmz.qualifier.service.InterfacePrint;
import com.lmz.qualifier.service.impl.PrintTest1;
import com.lmz.qualifier.service.impl.PrintTest2;

/**
 * @author ZLM
 *
 */
@Configuration
public class SpringBean {
	@Bean(value = "bean1")
	public InterfacePrint test1() {
		return new PrintTest1();
	}

	@Bean(value = "bean2")
	public InterfacePrint test2() {
		return new PrintTest2();
	}
}
