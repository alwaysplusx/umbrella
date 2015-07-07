/*
 * Copyright 2013-2015 wuxii@foxmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.message.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
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

import com.harmony.umbrella.message.Message;
import com.harmony.umbrella.message.MessageListener;

/**
 * @author wuxii@foxmail.com
 */
public class ChannelHandlerMessageListener extends ChannelHandlerAdapter implements MessageListener {

    private int port = 8080;

    private boolean initialed;

    private static final Logger log = LoggerFactory.getLogger(ChannelHandlerMessageListener.class);

    public ChannelHandlerMessageListener() {
    }

    @Override
    public void init() {

        if (initialed) {
            return;
        }

        initialed = true;

        final ChannelHandler messageHandler = this;

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
                                            messageHandler);
                                }
                            });

                    // Bind and start to accept incoming connections.
                    b.bind(port).sync().channel().closeFuture().sync();

                    System.out.println(">>>>>>>>>>>> start");
                } catch (InterruptedException e) {
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }

            }
        }.start();

    }

    @Override
    public void onMessage(Message message) {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Echo back the received object to the client.
        log.info("Echo back the received object to the client -> {}", msg);
        // ctx.write(msg);
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
    public void destory() {

    }

}
