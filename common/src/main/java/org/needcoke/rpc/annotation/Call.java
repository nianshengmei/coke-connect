package org.needcoke.rpc.annotation;

import java.lang.annotation.*;

/**
 * 声明coke-connect函数别名
 *
 * @author yanming
 * @date 2022/5/11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Call {
    String value();
}
