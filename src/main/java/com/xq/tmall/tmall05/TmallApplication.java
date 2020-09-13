package com.xq.tmall.tmall05;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class TmallApplication {

    public static void main(String[] args) {
        SpringApplication.run(TmallApplication.class, args);
    }


}
