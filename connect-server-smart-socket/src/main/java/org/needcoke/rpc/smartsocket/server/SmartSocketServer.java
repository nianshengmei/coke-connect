package org.needcoke.rpc.smartsocket.server;

import lombok.extern.slf4j.Slf4j;
import org.needcoke.rpc.config.ServerConfig;
import org.needcoke.rpc.smartsocket.processor.SmartSocketServerProcessor;
import org.needcoke.rpc.server.ConnectionServer;
import org.needcoke.rpc.smartsocket.codec.CokeRequestProtocol;
import org.smartboot.socket.transport.AioQuickServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
public class SmartSocketServer implements ConnectionServer {

    @Resource
    private ServerConfig serverConfig;

    private AioQuickServer server;


    @PostConstruct
    @Override
    public void start() throws IOException {
        server = new AioQuickServer(serverConfig.getCokeServerPort(), new CokeRequestProtocol(),new SmartSocketServerProcessor());
        server.setBannerEnabled(false);
        server.start();
        log.info("smart socket server start on port {}",serverConfig.getCokeServerPort());
    }

    @PreDestroy
    public void destroy(){
        server.shutdown();
    }
}
