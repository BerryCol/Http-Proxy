package com.apollo.httpproxyhandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpClientCodec;

/**
 * HTTP代理，转发解码后的HTTP报文
 */
public class HttpProxyInitializer extends ChannelInitializer {

  private Channel clientChannel;

  public HttpProxyInitializer(Channel clientChannel) {
    this.clientChannel = clientChannel;
  }

  @Override
  protected void initChannel(Channel ch) throws Exception {
    ch.pipeline().addLast("httpCodec", new HttpClientCodec());
    ch.pipeline().addLast("proxyClientHandle", new HttpProxyClientHandler(clientChannel));
  }
}
