package com.datacube;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author dale
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.datacube.mapper")
public class BISPServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BISPServerApplication.class, args);
    }

}
