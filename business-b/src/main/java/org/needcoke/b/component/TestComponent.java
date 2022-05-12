package org.needcoke.b.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@Component
public class TestComponent {

    private RpcClient client;

    @Autowired
    public void setClient(RpcClient client) {
        this.client = client;
    }

    public Object gg(String nm){
        return "cao "+ client.vvd();
    }
}
