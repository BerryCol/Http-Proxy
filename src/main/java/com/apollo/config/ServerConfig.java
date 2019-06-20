package com.apollo.config;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class ServerConfig {
    private static EventLoopGroup clinetGroup=new NioEventLoopGroup(8);
    private static int startport=8081;


    public static EventLoopGroup getClinetGroup(){
        return clinetGroup;
    }
    public static int getStartport(){
        String port=System.getProperty("port");
        if(null!=port && !"".equals(port)){
            startport=Integer.parseInt(port);
        }
        return startport;
    }
}
