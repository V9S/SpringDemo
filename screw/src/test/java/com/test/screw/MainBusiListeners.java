 package com.test.screw;

import org.springframework.context.ApplicationListener;

import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 启动监听
 */
public class MainBusiListeners implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContextUtils.setContext(event.getApplicationContext());
    }
}