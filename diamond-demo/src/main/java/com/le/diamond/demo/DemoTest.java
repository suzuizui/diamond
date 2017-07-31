package com.le.diamond.demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by gaobo3 on 2016/3/28.
 */
public class DemoTest {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-diamond.xml");
        DiamondDemoConfig diamondDemoConfig = (DiamondDemoConfig)context.getBean("diamondDemoConfig");
        System.out.println("DiamondDemoConfig, Old Config: " + diamondDemoConfig.getConfig());
        Thread.sleep(120 * 1000L);
        System.out.println("DiamondDemoConfig, New Config: " + diamondDemoConfig.getConfig());
    }
}
