package org.needcoke.a.configuration;

import lombok.extern.slf4j.Slf4j;
import org.needcoke.a.annotation.Call;
import org.springframework.context.annotation.Configuration;

/**
 * @author yanming
 * @date 2022/5/11
 */
@Configuration
@Slf4j
public class Config {

    @Call("haha1")
    public String haha(){
        log.info(this.getClass().getName()+":haha()被调用");
        return "haha";
    }


    @Call("hahha2")
    public String haha(String word){
        log.info(this.getClass().getName()+":say()被调用");
        return "say : "+word;
    }
}
