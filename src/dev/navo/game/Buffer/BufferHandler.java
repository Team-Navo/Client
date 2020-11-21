package dev.navo.game.Buffer;

import dev.navo.game.Tools.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class BufferHandler {
    private dev.navo.game.Client.EventBuffer event;
    private dev.navo.game.Client.InGameBuffer inGame;
    private LoginBuffer login;
    public BufferHandler() {
        this.event = dev.navo.game.Client.EventBuffer.getInstance();
        this.inGame= dev.navo.game.Client.InGameBuffer.getInstance();
        this.login=LoginBuffer.getInstance();
    }
    private String data;
    private boolean empty = true;
    public void put(String data) {
        try {
            JSONObject json= JsonParser.createJson(data);
            //2중 JSON 처리할까? 말까? 에 따라 나뉘어진다.
            switch((String)json.get("Header")) {
                case "LOGIN":
                case "CREATE":
                case "ID":
                case "PW":
                    this.login.put(data);
                    break;
                case "INGAME":
                    this.inGame.put(data);
                    break;
                case "EVENT":
                    this.event.put(data);
                    break;
            }
        } catch(ParseException e) {}
        finally {
            notifyAll();
        }
    }
}
