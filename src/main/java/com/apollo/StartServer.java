package com.apollo;

import com.apollo.config.ServerConfig;
import io.netty.channel.ChannelFuture;
import com.apollo.server.HttpProxyServer;

/**
 * 程序启动入口
 */

public class StartServer {

    public static void main(String[] args){
        HttpProxyServer httpProxyServer=new HttpProxyServer();
        try {
            ChannelFuture future=httpProxyServer.init().bind(ServerConfig.getStartport()).sync();
            System.out.println("server已经启动,使用端口--"+ServerConfig.getStartport());
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            httpProxyServer.shutdown();
        }

    }
}
