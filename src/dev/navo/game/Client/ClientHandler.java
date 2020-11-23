package dev.navo.game.Client;

import dev.navo.game.Buffer.EventBuffer;
import dev.navo.game.Buffer.InGameBuffer;
import dev.navo.game.Buffer.LoginBuffer;
import dev.navo.game.Tools.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.json.simple.JSONObject;

public class ClientHandler  extends ChannelInboundHandlerAdapter {

    EventBuffer eventBuffer = EventBuffer.getInstance();
    InGameBuffer inGameBuffer = InGameBuffer.getInstance();
    LoginBuffer loginBuffer = LoginBuffer.getInstance();

    @Override
    public void channelRead(ChannelHandlerContext arg0, Object arg1) throws Exception {
        String msg = arg1.toString();
        JSONObject json = JsonParser.createJson(msg);

        String header = json.get("Header").toString();

        switch(header) {
            case "Auth":
                loginBuffer.put(JsonParser.createJson(json.get("Body").toString()));
                break;
            case "Event" :
                eventBuffer.put(JsonParser.createJson(json.get("Body").toString()));
                break;
            case "InGame" : // update
                inGameBuffer.put(JsonParser.createJson(json.get("Body").toString()));
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}