package com.lmz.enablescheduling.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author ZLM 定时任务的类上加@Component注解，定时任务的方法上加 @Scheduled注解
 * @date 2020/05/21
 */
@Component
public class Scheduling {

    private long a = System.currentTimeMillis();

    @Scheduled(fixedRate = 3000)
    private void fun() {
        // 设置日期格式
        long time = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // new Date()为获取当前系统时间
        System.out.println("定时任务执行，间隔三秒:" + df.format(new Date()));
        System.out.println("time:" + time);
        System.out.println("a:" + a);
        System.out.println("(time - a):" + (time - a));
        System.out.println((time - a) / 1000);

        a = time;
    }

}
