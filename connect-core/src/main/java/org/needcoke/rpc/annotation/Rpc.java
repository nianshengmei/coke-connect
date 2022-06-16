package org.needcoke.rpc.annotation;

import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 远程调用类,远程调用方法
 *
 * @author Gilgamesh
 * @date 2022/4/2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Rpc {
    String value() default "";
    String beanName() default "";
}
