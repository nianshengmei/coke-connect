package org.needcoke.rpc.server;

import org.needcoke.rpc.codec.CokeRequestProtocol;
import org.needcoke.rpc.config.ServerConfig;
import org.needcoke.rpc.invoker.OkHttpsInvoker;
import org.needcoke.rpc.processor.SmartSocketServerProcessor;
import org.smartboot.socket.transport.AioQuickServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import java.io.IOException;

@ConditionalOnMissingBean(OkHttpsInvoker.class)
public class SmartSocketServer implements ConnectionServer{

    private ServerConfig serverConfig;

    private AioQuickServer server;

    public SmartSocketServer() {
        server = new AioQuickServer(serverConfig.getCokeServerPort(), new CokeRequestProtocol(),new SmartSocketServerProcessor());
    }

    @Override
    public void start() throws IOException {
        server.start();
    }
}
