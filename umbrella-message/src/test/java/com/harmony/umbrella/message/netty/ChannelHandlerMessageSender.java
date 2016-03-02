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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.message.Message;
import com.harmony.umbrella.message.MessageSender;

/**
 * @author wuxii@foxmail.com
 */
public class ChannelHandlerMessageSender implements MessageSender {

    private static final Log log = Logs.getLog(ChannelHandlerMessageSender.class);

    protected final String host;
    protected final int port;

    public ChannelHandlerMessageSender(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean send(final Message message) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)//
                    .channel(NioSocketChannel.class)//
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new ObjectEncoder(),//
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),//
                                    new ChannelMessageHandler(message));
                        }
                    });

            b.connect(host, port)//
                    .sync()//
                    .channel()//
                    .closeFuture().addListener(ChannelFutureListener.CLOSE);

        } catch (InterruptedException e) {
        } finally {
            group.shutdownGracefully();
        }
        return false;
    }

    private class ChannelMessageHandler extends ChannelHandlerAdapter {

        private Message message;

        public ChannelMessageHandler(Message message) {
            this.message = message;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            // Send the first message if this handler is a client-side handler.
            log.info("send message on channel active");
            ctx.writeAndFlush(message);
            log.info(">>>>");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            // Echo back the received object to the server.
            log.info("Echo back the received object to the server -> {}", msg);
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

    }

}
