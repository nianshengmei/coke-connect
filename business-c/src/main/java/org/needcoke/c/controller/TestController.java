package org.needcoke.c.controller;

import lombok.RequiredArgsConstructor;
import org.needcoke.rpc.utils.ConnectUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@RestController
@RequestMapping("api/c")
@RequiredArgsConstructor
public class TestController {


    @GetMapping("exec")
    public void exec() {
        for (int i = 0; i < 1000000; i++) {
            ConnectUtil.execute("bussiness-b", "testController", "test", null);
            if (i % 10000 == 0) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
