package com.opc.freshness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by perry on 6/26/17.
 */
@EnableFeignClients
@ServletComponentScan(value = "com.opc.freshness.*")
@EnableDiscoveryClient
@EnableTransactionManagement
@SpringBootApplication
@ImportResource({"classpath:dubbo.xml",
        "classpath:wmq-consumer.xml",
        "classpath:wmq-producer.xml",
        "classpath:spring-*.xml"})
public class Application extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}