package org.needcoke.b;

import org.needcoke.rpc.utils.SpringContextUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@EnableDiscoveryClient
@SpringBootApplication
public class BApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(BApplication.class, args);
        SpringContextUtils bean = run.getBean(SpringContextUtils.class);

        Object beanNameMethodMap = run.getBean("beanNameMethodMap");

        System.out.println(123);

    }
}
