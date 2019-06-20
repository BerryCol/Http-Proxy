package com.apollo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import com.apollo.serverhandler.HttpProxyServerhandler;

public class HttpProxyServer {
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup workGroup;
    private EventLoopGroup bossGroup;

    public ServerBootstrap init() {
        workGroup=new NioEventLoopGroup(8);
        bossGroup=new NioEventLoopGroup(1);
        serverBootstrap=new ServerBootstrap();
        serverBootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.TCP_NODELAY,true)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast("httpCodec",new HttpServerCodec());
                        ch.pipeline().addLast("serverHandle",new HttpProxyServerhandler());
                    }
                });

        return serverBootstrap;
    }

    public void shutdown(){
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
