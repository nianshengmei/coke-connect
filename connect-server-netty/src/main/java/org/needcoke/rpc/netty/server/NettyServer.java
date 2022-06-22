package org.needcoke.rpc.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.config.ServerConfig;
import org.needcoke.rpc.netty.codec.RpcDecoder;
import org.needcoke.rpc.netty.codec.RpcEncoder;
import org.needcoke.rpc.server.ConnectionServer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

public class NettyServer implements ConnectionServer {

    @Resource
    private ServerConfig serverConfig;


    @PostConstruct
    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); //bossGroup就是parentGroup，是负责处理TCP/IP连接的
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //workerGroup就是childGroup,是负责处理Channel(通道)的I/O事件

        ServerBootstrap sb = new ServerBootstrap();
        sb.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128) //初始化服务端可连接队列,指定了队列的大小128
                .childOption(ChannelOption.SO_KEEPALIVE, false) //保持长连接
                .childHandler(new ChannelInitializer<SocketChannel>() {  // 绑定客户端连接时候触发操作
                    @Override
                    protected void initChannel(SocketChannel sh) throws Exception {
                        sh.pipeline()
                                .addLast(new RpcDecoder(CokeRequest.class)) //解码request
                                .addLast(new RpcEncoder(CokeRequest.class)) //编码response
                                .addLast(new NettyServerHandlerAdapter()); //使用ServerHandler类来处理接收到的消息
                    }
                });
        //绑定监听端口，调用sync同步阻塞方法等待绑定操作完
        ChannelFuture future = null;
        try {
            future = sb.bind(serverConfig.getCokeServerPort()).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //成功绑定到端口之后,给channel增加一个 管道关闭的监听器并同步阻塞,直到channel关闭,线程才会往下执行,结束进程。
        try {
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }

}
