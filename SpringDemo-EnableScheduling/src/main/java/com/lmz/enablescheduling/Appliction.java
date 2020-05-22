 package com.lmz.enablescheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author ZLM @EnableScheduling注解用于开启spring boot自带的定时任务，放在配置类上表示开启定时任务，自动扫描
 * @date 2020/05/21
 */
@EnableScheduling
@SpringBootApplication
public class Appliction {

     public static void main(String[] args) {
         SpringApplication.run(Appliction.class,args);
    }
}
