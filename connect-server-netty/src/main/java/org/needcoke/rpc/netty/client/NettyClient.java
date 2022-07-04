package org.needcoke.rpc.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.netty.codec.RpcDecoder;
import org.needcoke.rpc.netty.codec.RpcEncoder;

public class NettyClient {

    private Channel channel;

    private String ip;

    private int port;

    public NettyClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Channel start() throws InterruptedException {

        final EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)// 使用NioSocketChannel来作为连接用的channel类
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500)
                .handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new RpcEncoder(CokeRequest.class)); //编码request
                        pipeline.addLast(new RpcDecoder(CokeRequest.class)); //解码response
                        pipeline.addLast(new SenderHandlerAdapter()); //客户端处理类

                    }
                });
        //发起异步连接请求，绑定连接端口和host信息
        final ChannelFuture future = b.connect(ip, port).sync();
        this.channel = future.channel();
        return this.channel;
    }
}
