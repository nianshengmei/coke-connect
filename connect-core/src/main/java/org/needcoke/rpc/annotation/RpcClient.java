package org.needcoke.rpc.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcClient {

    String name() default "";

    String value() default "";

    String beanName();

    String serviceId();



}
