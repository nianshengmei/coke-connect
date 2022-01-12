package org.needcoke.connect.receiver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.needcoke.connect.codec.CokeDecoder;
import org.needcoke.connect.codec.CokeEncoder;
import org.needcoke.connect.config.ReceiverConfig;
import org.needcoke.connect.protocol.CokeRequest;
import org.needcoke.connect.util.ConnectUtil;

import java.net.InetSocketAddress;
import java.util.TimerTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * coke-connect的接收器
 *
 * @author Gilgamesh
 * @since V1.0
 */
public class Receiver extends AbstractReceiver {

    /* 接收器的配置 */
    private ReceiverConfig receiverConfig;

    private final ServerBootstrap serverBootstrap;
    private final EventLoopGroup eventLoopGroupSelector;
    private final EventLoopGroup eventLoopGroupBoss;

    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    private ReceiverHandler handshakeHandler;

    private static final String HANDSHAKE_HANDLER_NAME = "handshakeHandler";

    public Receiver(ReceiverConfig receiverConfig,ReceiverHandler handshakeHandler){
        this.serverBootstrap = new ServerBootstrap();
        this.receiverConfig = receiverConfig;
        this.handshakeHandler = handshakeHandler;

        if (useEpoll()) {
            this.eventLoopGroupBoss = new EpollEventLoopGroup(1, new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyEPOLLBoss_%d", this.threadIndex.incrementAndGet()));
                }
            });

            this.eventLoopGroupSelector = new EpollEventLoopGroup(receiverConfig.getServerSelectorThreads(), new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);
                private int threadTotal = receiverConfig.getServerSelectorThreads();

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyServerEPOLLSelector_%d_%d", threadTotal, this.threadIndex.incrementAndGet()));
                }
            });
        } else {
            this.eventLoopGroupBoss = new NioEventLoopGroup(1, new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyNIOBoss_%d", this.threadIndex.incrementAndGet()));
                }
            });

            this.eventLoopGroupSelector = new NioEventLoopGroup(receiverConfig.getServerSelectorThreads(), new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);
                private int threadTotal = receiverConfig.getServerSelectorThreads();

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyServerNIOSelector_%d_%d", threadTotal, this.threadIndex.incrementAndGet()));
                }
            });
        }
    }

    public Receiver(){
        this(new ReceiverConfig(),null);
    }

    public Receiver(ReceiverHandler handshakeHandler){
        this(new ReceiverConfig(),handshakeHandler);
    }

    /* 是否采用Epoll */
    private boolean useEpoll() {
        return ConnectUtil.isLinuxPlatform()
                && receiverConfig.isUseEpollNativeSelector()
                && Epoll.isAvailable();
    }

    public void start(){
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(
                receiverConfig.getServerWorkerThreads(),
                new ThreadFactory() {

                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "NettyServerCodecThread_" + this.threadIndex.incrementAndGet());
                    }
                });

        ServerBootstrap childHandler =
                this.serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupSelector)
                        .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .option(ChannelOption.SO_REUSEADDR, true)
                        .option(ChannelOption.SO_KEEPALIVE, false)
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childOption(ChannelOption.SO_SNDBUF, receiverConfig.getServerSocketSndBufSize())
                        .childOption(ChannelOption.SO_RCVBUF, receiverConfig.getServerSocketRcvBufSize())
                        .localAddress(new InetSocketAddress(this.receiverConfig.getListenPort()))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline()
                                        .addLast(defaultEventExecutorGroup, HANDSHAKE_HANDLER_NAME, handshakeHandler)
                                        .addLast(defaultEventExecutorGroup,
                                                new CokeEncoder(CokeRequest.class),
                                                new CokeDecoder(CokeRequest.class),
                                                new IdleStateHandler(0, 0, nettyServerConfig.getServerChannelMaxIdleTimeSeconds()),
                                                connectionManageHandler,
                                                serverHandler
                                        );
                            }
                        });

        if (nettyServerConfig.isServerPooledByteBufAllocatorEnable()) {
            childHandler.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        }

        try {
            ChannelFuture sync = this.serverBootstrap.bind().sync();
            InetSocketAddress addr = (InetSocketAddress) sync.channel().localAddress();
            this.port = addr.getPort();
        } catch (InterruptedException e1) {
            throw new RuntimeException("this.serverBootstrap.bind().sync() InterruptedException", e1);
        }

        if (this.channelEventListener != null) {
            this.nettyEventExecutor.start();
        }

        this.timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                try {
                    NettyRemotingServer.this.scanResponseTable();
                } catch (Throwable e) {
                    log.error("scanResponseTable exception", e);
                }
            }
        }, 1000 * 3, 1000);
    }


}
