package org.needcoke.rpc.annotation;

import java.lang.annotation.*;

/**
 * 远程调用类,远程调用方法
 *
 * @author Gilgamesh
 * @date 2022/4/2
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Rpc {

    String value() default "";

    String serviceId() default "";

    String beanName() default "";

    String callName() default "";
}
