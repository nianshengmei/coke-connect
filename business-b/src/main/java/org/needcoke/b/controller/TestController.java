package org.needcoke.b.controller;

import lombok.RequiredArgsConstructor;
import org.needcoke.rpc.utils.ConnectUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@RestController
@RequestMapping("api/b")
@RequiredArgsConstructor
public class TestController {

    @GetMapping("test")
    public String test(){
        Map<String,Object> map = new HashMap<>();
        map.put("word","严鸣是死废物");
        return ConnectUtil.execute("bussiness-a","config","hahha2",map);
    }
}
