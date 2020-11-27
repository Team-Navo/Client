package dev.navo.game.Client;

import dev.navo.game.Buffer.EventBuffer;
import dev.navo.game.Buffer.InGameBuffer;
import dev.navo.game.Buffer.LoginBuffer;
import dev.navo.game.Tools.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    // buffer 사용X --> 스레드
    EventBuffer eventBuffer = EventBuffer.getInstance();
    InGameBuffer inGameBuffer = InGameBuffer.getInstance();
    LoginBuffer loginBuffer = LoginBuffer.getInstance();

    @Override
    public void channelRead(ChannelHandlerContext arg0, Object arg1) throws Exception {
        String msg = arg1.toString();
        JSONObject json = JsonParser.createJson(msg);

        String header = json.get("Header").toString();

        switch(header) {
            case "Auth": // 로그인 작업류
                loginBuffer.put(json);
                break;
            case "Update": // 구 InGame, 게임 진행 중 변경 사항 업데이트
                inGameBuffer.put(JsonParser.createJson(json.get("Body").toString()));
                break;
            case "Event": // 초기화, 충돌 감지
                eventHandler(json);
                break;
        }
    }

    private void eventHandler(JSONObject json) throws ParseException {
        String function = json.get("Function").toString();
        JSONObject body = (JsonParser.createJson(json.get("Body").toString()));

        switch (function) {
            case "0": // 나의 crewmates Enter
                Room.getRoom().roomInit(json);
                break;
            case "1": // 다른 crewmates Enter
                Room.getRoom().roomNewUserEnter(body);
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
