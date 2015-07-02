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
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import com.harmony.umbrella.message.Message;
import com.harmony.umbrella.message.MessageSender;

/**
 * @author wuxii@foxmail.com
 */
public class ChannelHandlerMessageSender implements MessageSender {

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
                            p.addLast(new ChannelMessageHandler(message));
                        }
                    });

            // Start the connection attempt.
            ChannelFuture future = b.connect(host, port).sync().channel().closeFuture();
            future.addListener(ChannelFutureListener.CLOSE);

        } catch (InterruptedException e) {
        } finally {
            group.shutdownGracefully();
        }
        return false;
    }

    private class ChannelMessageHandler extends ChannelHandlerAdapter {

        private Object message;

        public ChannelMessageHandler(Message message) {
            this.message = message;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(message);
        }
    }

}
