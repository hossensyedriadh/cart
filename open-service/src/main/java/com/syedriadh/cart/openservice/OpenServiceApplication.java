package com.syedriadh.cart.openservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@EnableTransactionManagement
@EnableDiscoveryClient
@SpringBootApplication
public class OpenServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenServiceApplication.class, args);
    }

}
