package org.needcoke.rpc.server;

import lombok.extern.slf4j.Slf4j;
import org.needcoke.rpc.codec.CokeRequestProtocol;
import org.needcoke.rpc.config.ServerConfig;
import org.needcoke.rpc.invoker.OkHttpsInvoker;
import org.needcoke.rpc.processor.SmartSocketServerProcessor;
import org.smartboot.socket.transport.AioQuickServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Component
@ConditionalOnMissingBean(OkHttpsInvoker.class)
public class SmartSocketServer implements ConnectionServer{

    @Resource
    private ServerConfig serverConfig;

    private AioQuickServer server;


    @PostConstruct
    @Override
    public void start() throws IOException {
        server = new AioQuickServer(serverConfig.getCokeServerPort(), new CokeRequestProtocol(),new SmartSocketServerProcessor());
        server.start();
        log.info("smart socket server start on port {}",serverConfig.getCokeServerPort());
    }
}
