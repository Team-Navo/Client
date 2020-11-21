package dev.navo.game.Client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler  extends ChannelInboundHandlerAdapter {

    dev.navo.game.Client.Buffer buffer;

    public ClientHandler(dev.navo.game.Client.Buffer buffer){
        this.buffer = buffer;
    }

    @Override
    public void channelRead(ChannelHandlerContext arg0, Object arg1) throws Exception {
        buffer.put(arg1.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
