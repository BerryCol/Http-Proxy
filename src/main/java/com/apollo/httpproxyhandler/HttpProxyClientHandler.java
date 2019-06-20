package com.apollo.httpproxyhandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class HttpProxyClientHandler extends ChannelInboundHandlerAdapter {

  private Channel clientChannel;

  public HttpProxyClientHandler(Channel clientChannel) {
    this.clientChannel = clientChannel;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    //客户端channel已关闭则不转发了
    if (!clientChannel.isOpen()) {
      ReferenceCountUtil.release(msg);
      return;
    }
    clientChannel.writeAndFlush(msg);
  }

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx)  {
    ctx.channel().close();
    clientChannel.close();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.channel().close();
    clientChannel.close();
  }
}
