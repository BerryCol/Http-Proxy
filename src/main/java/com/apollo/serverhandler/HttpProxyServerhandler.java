package com.apollo.serverhandler;

import com.apollo.config.ServerConfig;
import com.apollo.httpproxyhandler.HttpProxyInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import com.apollo.uitl.ProtoUtil;

import java.util.LinkedList;
import java.util.List;

public class HttpProxyServerhandler extends ChannelInboundHandlerAdapter{

    /**
     * 代理客户端连接返回结果
     */
    private ChannelFuture channelFuture;
    private int status=0;
    private boolean isConnect=false;
    private List requestList;
    private String host;
    private int port;

    public HttpProxyServerhandler() {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //是单独请求
        if (msg instanceof HttpRequest){

            final HttpRequest request= (HttpRequest) msg;
            if(status==0){
                ProtoUtil.RequestProto requestProto=ProtoUtil.getRequestProto(request);
                //关掉异常连接
                if(requestProto ==null){
                    ctx.channel().close();
                    return;
                }
                this.host=requestProto.getHost();
                this.port=requestProto.getPort();
                forwardingData(ctx.channel(),request);
            }
        }else if (msg instanceof HttpContent){
            if(status==1){
                try {
                    forwardingData(ctx.channel(),msg);
                }catch (Exception e){
                    ReferenceCountUtil.release(msg);
                    status = 0;
                }
            }else {
                ReferenceCountUtil.release(msg);
                status = 0;
            }
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
        ctx.channel().close();
    }

    private void forwardingData(Channel channel,Object msg) throws Exception{

        if(channelFuture==null){

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(ServerConfig.getClinetGroup()) // 注册线程池
                    .channel(NioSocketChannel.class) // 使用NioSocketChannel来作为连接用的channel类
                    .handler(new HttpProxyInitializer(channel));

            requestList = new LinkedList();
            channelFuture = bootstrap.connect(host, port);
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    future.channel().writeAndFlush(msg);
                    synchronized (requestList) {
                        requestList.forEach((obj) -> future.channel().writeAndFlush(obj));
                        requestList.clear();
                        isConnect = true;
                    }
                } else {
                    requestList.forEach((obj) -> ReferenceCountUtil.release(obj));
                    requestList.clear();
                    future.channel().close();
                    channel.close();
                }
            });
        } else {
            synchronized (requestList) {
                if (isConnect) {
                    channelFuture.channel().writeAndFlush(msg);
                } else {
                    requestList.add(msg);
                }
            }
        }
    }
}
