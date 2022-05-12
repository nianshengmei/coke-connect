package org.needcoke.a.configuration;

import lombok.extern.slf4j.Slf4j;
import org.needcoke.rpc.annotation.Call;
import org.needcoke.rpc.annotation.Rpc;

/**
 *
 * @author yanming
 * @date 2022/5/11
 */
@Rpc
@Slf4j
public class Config {

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
