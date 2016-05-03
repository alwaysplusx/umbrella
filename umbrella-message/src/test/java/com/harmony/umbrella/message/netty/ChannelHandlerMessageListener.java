package com.harmony.umbrella.message.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.message.Message;
import com.harmony.umbrella.message.MessageListener;

/**
 * @author wuxii@foxmail.com
 */
public class ChannelHandlerMessageListener extends ChannelHandlerAdapter implements MessageListener {

    public static final int DEFAULT_PORT = 18160;

    private int port = DEFAULT_PORT;

    private static final Log log = Logs.getLog(ChannelHandlerMessageListener.class);

    private ChannelFuture channelFuture;

    public ChannelHandlerMessageListener() {
        this(DEFAULT_PORT);
    }

    public ChannelHandlerMessageListener(int port) {
        this.port = port;
    }

    @Override
    public void init() {
        if (channelFuture == null) {
            synchronized (ChannelHandlerMessageListener.class) {
                if (channelFuture == null) {
                    new Thread("ChannelHandler-Thread") {
                        @Override
                        public void run() {

                            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
                            EventLoopGroup workerGroup = new NioEventLoopGroup();
                            try {

                                ServerBootstrap b = new ServerBootstrap();
                                b.group(bossGroup, workerGroup)//
                                        .channel(NioServerSocketChannel.class)//
                                        .handler(new LoggingHandler(LogLevel.INFO))//
                                        .childHandler(new ChannelInitializer<SocketChannel>() {
                                            @Override
                                            public void initChannel(SocketChannel ch) throws Exception {
                                                ChannelPipeline p = ch.pipeline();
                                                p.addLast(new ObjectEncoder(),//
                                                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),//
                                                        ChannelHandlerMessageListener.this);
                                            }
                                        });

                                channelFuture = b.bind(port);
                                channelFuture.sync().channel().closeFuture().sync();

                            } catch (InterruptedException e) {
                            } finally {
                                bossGroup.shutdownGracefully();
                                workerGroup.shutdownGracefully();
                            }

                        }
                    }.start();
                }
            }
        }

    }

    @Override
    public void onMessage(Message message) {
        log.info("{}", message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Message) {
            onMessage((Message) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void destroy() {
        channelFuture.channel().close();
        channelFuture = null;
    }

}
