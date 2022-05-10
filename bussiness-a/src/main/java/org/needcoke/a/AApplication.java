package org.needcoke.a;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@EnableDiscoveryClient
@SpringBootApplication
public class AApplication {

    public static void main(String[] args) {
        SpringApplication.run(AApplication.class,args);
    }
}
