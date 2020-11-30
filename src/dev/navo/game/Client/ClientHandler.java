package dev.navo.game.Client;

import dev.navo.game.Buffer.LoginBuffer;
import dev.navo.game.Scenes.Hud;
import dev.navo.game.Sprites.Character.CrewmateMulti;
import dev.navo.game.Tools.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext arg0, Object arg1) throws Exception {
        String msg = arg1.toString();
        JSONObject json = JsonParser.createJson(msg);

        String header = json.get("Header").toString();

        switch(header) {
            case "Auth": // 로그인 작업류
                LoginBuffer.getInstance().put(json);
                break;
            case "Update": // 구 InGame, 게임 진행 중 변경 사항 업데이트
                updateHandler(json);
                //inGameBuffer.put(JsonParser.createJson(json.get("Body").toString()));
                break;
            case "Event": // 초기화, 방장, 색 변경, 충돌 감지
                eventHandler(json);
                break;
        }
    }

    private void updateHandler(JSONObject json) {

    }

    private void eventHandler(JSONObject json) {
        String function = json.get("Function").toString();
        System.out.println("eventHandler json : " + json);
        switch (function) {
            case "0": // 나의 crewmate + 접속해 있던 crewmate 생성
                Room.getRoom().roomInit(json);
                break;
            case "1": // 새로 접속한 crewmate 생성
                Room.getRoom().roomNewUserEnter((JSONObject)json.get("Body"));
                break;
            case "2": // 색 변경
                Room.getRoom().changeUserColor((JSONObject)json.get("Body"));
                break;
            case "4":
                Room.getRoom().deleteUser(json.get("Body").toString());
                break;

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}