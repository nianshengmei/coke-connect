package org.needcoke.b;

import org.needcoke.b.client.ConClient;
import org.needcoke.rpc.annotation.EnableRpcClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@EnableRpcClient(basePackages = {"org.needcoke.b.client"})
@EnableDiscoveryClient
@SpringBootApplication
public class BApplication {

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext run = SpringApplication.run(BApplication.class, args);
            Object conClient2 = run.getBean(ConClient.class);
            System.out.println(123);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
